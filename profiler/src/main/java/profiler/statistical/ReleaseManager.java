package profiler.statistical;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages information about several releases
 * 
 * @author Pedro Oliveira <pedro@clarkparsia.com>
 */
public class ReleaseManager
{

	private final List<Release> releases;

	public ReleaseManager()
	{
		releases = new ArrayList<>();
	}

	private void sort()
	{
		Collections.sort(releases, (o1, o2) -> o2.getReleaseDate().compareTo(o1.getReleaseDate()));
	}

	public Release getLatestRelease()
	{
		if (releases.size() > 0)
			return releases.get(0);
		return null;
	}

	public Release getRelease(final int index)
	{
		return releases.get(index);
	}

	public List<Release> getReleases()
	{
		return releases;
	}

	public void load(final String filename)
	{
		final File f = new File(filename);

		if (f.isDirectory())
		{
			for (final File file : f.listFiles())
				if (file.isFile() && !file.isHidden())
					addReleaseFromFile(file.getAbsolutePath());
		}
		else
			addReleaseFromFile(filename);

		sort();
	}

	private void addReleaseFromFile(final String filename)
	{
		try
		{
			final Release r = ReleaseUtils.readFromFile(filename);
			releases.add(r);
		}
		catch (final Exception e)
		{
			System.err.println("Problem reading release information file " + filename);
			e.printStackTrace();
		}
	}
}
