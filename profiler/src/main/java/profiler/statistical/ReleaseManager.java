package profiler.statistical;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Manages information about several releases
 * @author Pedro Oliveira <pedro@clarkparsia.com>
 *
 */
public class ReleaseManager {

	private List<Release> releases;

	public ReleaseManager() {
		releases = new ArrayList<Release>();
	}

	private void sort() 
	{
		Collections.sort(releases, new Comparator<Release>(){
			public int compare(Release o1, Release o2) {
				return o2.getReleaseDate().compareTo(o1.getReleaseDate());
			}
		});
	}

	public Release getLatestRelease()
	{
		if(releases.size() > 0)
			return releases.get(0);
		return null;
	}

	public Release getRelease(int index){
		return releases.get(index);
	}

	public List<Release> getReleases() {
		return releases;
	}

	public void load(String filename)
	{
		File f = new File(filename);

		if(f.isDirectory())
		{
			for(File file: f.listFiles())
			{
				if(file.isFile() && !file.isHidden())
					addReleaseFromFile(file.getAbsolutePath());
			}
		}
		else
			addReleaseFromFile(filename);

		sort();
	}

	private void addReleaseFromFile(String filename)
	{
		try
		{
			Release r = ReleaseUtils.readFromFile(filename);
			releases.add(r);
		}catch (Exception e) {
			System.err.println("Problem reading release information file "+filename);
			e.printStackTrace();
		}
	}
}