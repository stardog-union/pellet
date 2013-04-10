package profiler.statistical;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mindswap.pellet.utils.VersionInfo;

/**
 * Contains information about the performance of a certain release
 * @author Pedro Oliveira <pedro@clarkparsia.com>
 *
 */
public class Release{
	
	private String version;
	private Long releaseDate;
	
	private Map<String, List<ReleaseStatistics>> statistics;	
	
	public Release(String version, Long releaseDate)
	{
		this.version = version;
		this.releaseDate = releaseDate;
		statistics = new LinkedHashMap<String, List<ReleaseStatistics>>();
	}
	
	public Release()
	{
		this(VersionInfo.getInstance().getVersionString(), System.currentTimeMillis());
	}
	
	public String getVersion()
	{
		return version;
	}
	
	public Long getReleaseDate()
	{
		return releaseDate;
	}
	
	public List<ReleaseStatistics> getStatistics(String ontology)
	{
		return statistics.get(ontology);
	}
	
	public void addStatistics(String ontology, List<ReleaseStatistics> statistics)
	{
		this.statistics.put(ontology, statistics);
	}

	public Map<String, List<ReleaseStatistics>> getAllStatistics() {
		return statistics;
	}
	
	public String toString() {
		return version;
	}
}
