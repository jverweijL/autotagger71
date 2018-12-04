package com.liferay.demo.autotagging.service.config;

import aQute.bnd.annotation.metatype.Meta;



@Meta.OCD(id = "com.liferay.demo.autotagging.service.config.AutoTaggingConfiguration")
public interface AutoTaggingConfiguration {
    @Meta.AD(
            deflt = "localhost",
            required = false
    )
    public String ElasticHost();

    @Meta.AD(
            deflt = "9200",
            required = false
    )
    public int ElasticPort();

    @Meta.AD(
            deflt = "http",
            required = false
    )
    public String ElasticProtocol();

    @Meta.AD(
            deflt = "liferay-autotagger",
            required = false
    )
    public String ElasticIndex();

    @Meta.AD(
            deflt = "myPercolator",
            required = false
    )
    public String ElasticType();

    @Meta.AD(
            deflt = "LiferayElasticsearchCluster",
            required = false
    )
    public String ElasticClusterName();


}