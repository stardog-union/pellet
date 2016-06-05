package profiler.statistical;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StatisticalLineAndShapeRenderer;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.ui.RefineryUtilities;
import profiler.ProfileKB.Task;

public class ReleasePerformanceVisualizer extends JFrame
{
	private static final long serialVersionUID = 3529811414164984003L;
	private static String _REPOSITORY;

	public static void main(final String[] args) throws IOException
	{
		final Properties properties = new Properties();
		properties.load(new FileInputStream("profiler/releasevisualizer.properties"));
		_REPOSITORY = properties.getProperty("REPOSITORY", "profiler/releases");

		final ReleasePerformanceVisualizer viz = new ReleasePerformanceVisualizer("Release Performance Visualizer");
		viz.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		viz.pack();
		RefineryUtilities.centerFrameOnScreen(viz);
		viz.setVisible(true);
	}

	private final JPanel _mainPanel;
	private JPanel _chart;
	private final MenuPanel _menu;
	private final ReleaseManager _manager;

	public ReleasePerformanceVisualizer(final String title)
	{
		super(title);

		_manager = new ReleaseManager();
		_manager.load(_REPOSITORY);

		_mainPanel = new JPanel();
		_mainPanel.setLayout(new BorderLayout());

		_menu = new MenuPanel(_manager);
		_mainPanel.add(_menu, BorderLayout.EAST);

		_chart = createChartPanel();
		_mainPanel.add(_chart, BorderLayout.CENTER);

		setContentPane(_mainPanel);
	}

	private DefaultStatisticalCategoryDataset createDataset()
	{
		final DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();
		int count = 0;

		final List<Release> releases = _manager.getReleases();
		for (int i = releases.size() - 1; i >= 0; i--)
		{
			final Release release = releases.get(i);
			if (_menu._releases.isSelectedIndex(count))
			{
				final List<ReleaseStatistics> stats = release.getStatistics((String) _menu._ontology.getSelectedItem());
				if (stats != null)
					for (final ReleaseStatistics stat : stats)
						if (_menu._tasks.isSelectedIndex(stat.getTask().ordinal()))
						{
							double mean, variance;

							if (_menu._time.getSelectedIndex() == 0)
							{
								mean = stat.getTimeStat("avg");
								variance = stat.getTimeStat("var");
							}
							else
							{
								mean = stat.getMemStat("avg");
								variance = stat.getMemStat("var");
							}
							dataset.add(mean, Math.sqrt(variance), stat.getTask().toString(), release.getVersion());
						}
			}
			count++;
		}
		return dataset;
	}

	private JFreeChart createChart(final DefaultStatisticalCategoryDataset dataset)
	{
		final CategoryItemRenderer renderer = new StatisticalLineAndShapeRenderer(true, true);
		//CategoryItemRenderer renderer = new StatisticalBarRenderer();

		for (int i = 0; i < dataset.getRowCount(); i++)
			renderer.setSeriesStroke(i, new BasicStroke(5f));

		final String numberAxisLabel = _menu._time.getSelectedIndex() == 0 ? "Execution Time (s)" : "Used Memory (%)";
		final CategoryPlot plot = new CategoryPlot(dataset, new CategoryAxis("Release"), new NumberAxis(numberAxisLabel), renderer);
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);
		plot.setForegroundAlpha(0.8f);

		return new JFreeChart((String) _menu._ontology.getSelectedItem(), plot);
	}

	public JPanel createChartPanel()
	{
		final JFreeChart chart = createChart(createDataset());
		return new ChartPanel(chart);
	}

	/**
	 * JPanel that encapsulates all the components in the menu
	 * 
	 * @author Pedro Oliveira <pedro@clarkparsia.com>
	 */
	private class MenuPanel extends JPanel
	{
		private static final long serialVersionUID = 8213647324959034612L;
		JComboBox<?> _time;
		JList<?> _tasks;
		JComboBox<?> _ontology;
		JList<?> _releases;
		JButton _ok;

		public MenuPanel(final ReleaseManager manager)
		{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			//DesignGridLayout

			//Time Menu
			_time = new JComboBox<Object>(new String[] { "Time", "Memory" });
			_time.setBorder(BorderFactory.createTitledBorder("Statistic"));
			add(_time);
			add(Box.createVerticalGlue());

			//Tasks Menu
			_tasks = new JList<Object>(Task.values());
			_tasks.setSelectionInterval(0, _tasks.getModel().getSize() - 1);
			_tasks.setBorder(BorderFactory.createTitledBorder("Task"));
			_tasks.setVisibleRowCount(5);
			add(new JScrollPane(_tasks));
			add(Box.createVerticalGlue());

			//Ontology Menu
			final Set<String> ontologies = new HashSet<>();
			for (final Release release : manager.getReleases())
				for (final Entry<String, List<ReleaseStatistics>> statistic : release.getAllStatistics().entrySet())
					ontologies.add(statistic.getKey());
			_ontology = new JComboBox<>(ontologies.toArray());
			_ontology.setBorder(BorderFactory.createTitledBorder("Ontology"));
			add(_ontology);
			add(Box.createVerticalGlue());

			//Releases menu
			_releases = new JList<>(manager.getReleases().toArray());
			_releases.setSelectionInterval(0, _releases.getModel().getSize() - 1);
			_releases.setBorder(BorderFactory.createTitledBorder("Releases"));
			_releases.setVisibleRowCount(5);
			add(new JScrollPane(_releases));
			add(Box.createVerticalGlue());

			//Ok button
			_ok = new JButton("Draw");
			_ok.addActionListener(e ->
			{
				_mainPanel.remove(_chart);
				_chart = createChartPanel();
				_mainPanel.add(_chart, BorderLayout.CENTER);
				_mainPanel.validate();
			});
			add(_ok);
		}
	}

}
