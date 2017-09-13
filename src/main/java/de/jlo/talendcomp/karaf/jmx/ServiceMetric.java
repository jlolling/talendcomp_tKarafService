package de.jlo.talendcomp.karaf.jmx;

public class ServiceMetric {
	
	private String serviceName = null;
	private Double meanDuration = null;
	private Long count = null;
	private Long countLast = null;
	private long timestamp = System.currentTimeMillis();
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String name) {
		this.serviceName = name;
	}
	public Double getDurationMean() {
		return meanDuration;
	}
	public void setDurationMean(Double value) {
		this.meanDuration = value;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public String toString() {
		return timestamp + "|" + serviceName + "| duration-mean:" + meanDuration + "| count:" + count + "| count-diff:" + getCountDiff();
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	@Override
	public boolean equals(Object o) {
		if (o instanceof ServiceMetric) {
			return ((ServiceMetric) o).serviceName.equals(serviceName);
		}
		return false;
	}
	public Long getCountLast() {
		return countLast;
	}
	public void setCountLast(Long countDiff) {
		this.countLast = countDiff;
	}
	public Long getCountDiff() {
		if (count != null) {
			if (countLast != null) {
				return count - countLast;
			} else {
				return 0l;
			}
		} else {
			return 0l;
		}
	}

}
