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

	private final String _version;
	private final Long _releaseDate;

	private final Map<String, List<ReleaseStatistics>> _statistics = new LinkedHashMap<>();

	public Release(final String version, final Long releaseDate)
	{
		_version = version;
		_releaseDate = releaseDate;
	}

	public Release()
	{
		this(VersionInfo.getInstance().getVersionString(), System.currentTimeMillis());
	}

	public String getVersion()
	{
		return _version;
	}

	public Long getReleaseDate()
	{
		return _releaseDate;
	}

	public List<ReleaseStatistics> getStatistics(final String ontology)
	{
		return _statistics.get(ontology);
	}

	public void addStatistics(final String ontology, final List<ReleaseStatistics> statistics)
	{
		this._statistics.put(ontology, statistics);
	}

	public Map<String, List<ReleaseStatistics>> getAllStatistics()
	{
		return _statistics;
	}

	@Override
	public String toString()
	{
		return _version;
	}
}
