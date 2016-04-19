package profiler.statistical;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.mindswap.pellet.utils.VersionInfo;

/**
 * Contains information about the performance of a certain release
 * 
 * @author Pedro Oliveira <pedro@clarkparsia.com>
 */
public class Release
{

	private final String version;
	private final Long releaseDate;

	private final Map<String, List<ReleaseStatistics>> statistics;

	public Release(final String version, final Long releaseDate)
	{
		this.version = version;
		this.releaseDate = releaseDate;
		statistics = new LinkedHashMap<>();
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

	public List<ReleaseStatistics> getStatistics(final String ontology)
	{
		return statistics.get(ontology);
	}

	public void addStatistics(final String ontology, final List<ReleaseStatistics> statistics)
	{
		this.statistics.put(ontology, statistics);
	}

	public Map<String, List<ReleaseStatistics>> getAllStatistics()
	{
		return statistics;
	}

	@Override
	public String toString()
	{
		return version;
	}
}
