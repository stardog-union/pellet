package profiler.statistical;


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

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

@SuppressWarnings("serial")
public class ReleasePerformanceVisualizer extends JFrame{

	private static String REPOSITORY;

	public static void main(String[] args) throws IOException{
		Properties properties = new Properties();
		properties.load(new FileInputStream("profiler/releasevisualizer.properties"));
		REPOSITORY = properties.getProperty("REPOSITORY", "profiler/releases");
		
		ReleasePerformanceVisualizer viz = new ReleasePerformanceVisualizer("Release Performance Visualizer");		
		viz.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		viz.pack();
		RefineryUtilities.centerFrameOnScreen(viz);
		viz.setVisible(true);
	}

	private JPanel mainPanel;
	private JPanel chart;
	private MenuPanel menu;
	private ReleaseManager manager;

	public ReleasePerformanceVisualizer(String title)
	{
		super(title);

		manager = new ReleaseManager();
		manager.load(REPOSITORY);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		menu = new MenuPanel(manager);
		mainPanel.add(menu, BorderLayout.EAST);
		
		chart = createChartPanel();
		mainPanel.add(chart, BorderLayout.CENTER);
		
		setContentPane(mainPanel);
	}

	private DefaultStatisticalCategoryDataset createDataset()
	{
		DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();	
		int count = 0;
		
		List<Release> releases = manager.getReleases();
		for(int i = releases.size()-1; i>=0 ; i--)
		{
			Release release = releases.get(i);
			if(menu.releases.isSelectedIndex(count))
			{
				List<ReleaseStatistics> stats = release.getStatistics((String)menu.ontology.getSelectedItem());
				if(stats != null)
				{
					for(ReleaseStatistics stat: stats)
					{
						if(menu.tasks.isSelectedIndex(stat.getTask().ordinal()))
						{
							double mean, variance;

							if(menu.time.getSelectedIndex() == 0){
								mean = stat.getTimeStat("avg");
								variance = stat.getTimeStat("var");
							}else{
								mean = stat.getMemStat("avg");
								variance = stat.getMemStat("var");
							}
							dataset.add(mean, Math.sqrt(variance), stat.getTask().toString(), release.getVersion());	
						}
					}
				}
			}
			count++;
		}		
		return dataset;
	}

	private JFreeChart createChart(DefaultStatisticalCategoryDataset dataset)
	{
		CategoryItemRenderer renderer = new StatisticalLineAndShapeRenderer(true,true);
		//CategoryItemRenderer renderer = new StatisticalBarRenderer();

		for(int i=0; i< dataset.getRowCount(); i++)
			renderer.setSeriesStroke(i, new BasicStroke(5f));

		String numberAxisLabel = menu.time.getSelectedIndex() == 0? "Execution Time (s)":"Used Memory (%)";
		CategoryPlot plot = new CategoryPlot(dataset, new CategoryAxis("Release"), new NumberAxis(numberAxisLabel), renderer);
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);    
		plot.setForegroundAlpha(0.8f);

		return new JFreeChart((String)menu.ontology.getSelectedItem(),plot);
	}

	public JPanel createChartPanel() {
		JFreeChart chart = createChart(createDataset());
		return new ChartPanel(chart);
	}


	/**
	 * JPanel that encapsulates all the components in the menu
	 * @author Pedro Oliveira <pedro@clarkparsia.com>
	 *
	 */
	private class MenuPanel extends JPanel
	{
		JComboBox time;
		JList tasks;
		JComboBox ontology;
		JList releases;
		JButton ok;

		public MenuPanel(ReleaseManager manager)
		{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			//DesignGridLayout
			
			//Time Menu
			time = new JComboBox(new String[]{"Time","Memory"});
			time.setBorder(BorderFactory.createTitledBorder("Statistic"));
			add(time);			
			add(Box.createVerticalGlue());
			
			//Tasks Menu
			tasks = new JList(Task.values());
			tasks.setSelectionInterval(0, tasks.getModel().getSize()-1);
			tasks.setBorder(BorderFactory.createTitledBorder("Task"));
			tasks.setVisibleRowCount(5);			
			add(new JScrollPane(tasks));
			add(Box.createVerticalGlue());
			
			//Ontology Menu	
			Set<String> ontologies = new HashSet<String>();
			for(Release release: manager.getReleases())
			{
				for(Entry<String, List<ReleaseStatistics>> statistic: release.getAllStatistics().entrySet())
					ontologies.add(statistic.getKey());
			}
			ontology = new JComboBox(ontologies.toArray());	
			ontology.setBorder(BorderFactory.createTitledBorder("Ontology"));
			add(ontology);
			add(Box.createVerticalGlue());

			//Releases menu
			releases = new JList(manager.getReleases().toArray());
			releases.setSelectionInterval(0, releases.getModel().getSize()-1);
			releases.setBorder(BorderFactory.createTitledBorder("Releases"));
			releases.setVisibleRowCount(5);			
			add(new JScrollPane(releases));
			add(Box.createVerticalGlue());

			//Ok button
			ok = new JButton("Draw");
			ok.addActionListener(new ActionListener(){			
				public void actionPerformed(ActionEvent e) {
					mainPanel.remove(chart);
					chart = createChartPanel();
					mainPanel.add(chart, BorderLayout.CENTER);
					mainPanel.validate();
				}
			});
			add(ok);
		}
	}

}
