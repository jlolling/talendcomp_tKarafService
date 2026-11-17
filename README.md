# Components to handle Talend or Talaxie based web services in a Karaf container
Deployment of Talend services made by the TAC in a Karaf-Container in a reliable and efficient way.

|Component               |Purpose                                                                                      |
|------------------------|---------------------------------------------------------------------------------------------|
|tKarafServiceInstall    |install a service (the complete feature) and take care to have only one bundle version active|
|tKarafServiceList       |List the services in a Karaf container (Talaxie or Talend Runtime)                           | 
|tKarafServiceMetricInput|Various metrics of the services like number calls and durations                              |

## Some example jobs:
### tKarafServiceList
![Example job](https://github.com/jlolling/talendcomp_tKarafService/blob/master/doc/read_services_from_karaf.png)

### tKarafServiceInstall
![Example job](https://github.com/jlolling/talendcomp_tKarafService/blob/master/doc/tKarafService_roundtrip.png)

### tKarafServiceMetricInput
![Example job](https://github.com/jlolling/talendcomp_tKarafService/blob/master/doc/reading_generic_jvm_metrics.png)
