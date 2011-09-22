package org.apache.stanbol.enhancer.engines.keywordextraction.linking.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.stanbol.enhancer.engines.keywordextraction.linking.EntitySearcher;
import org.apache.stanbol.entityhub.servicesapi.model.Entity;
import org.apache.stanbol.entityhub.servicesapi.model.Representation;
import org.apache.stanbol.entityhub.servicesapi.query.FieldQuery;
import org.apache.stanbol.entityhub.servicesapi.query.QueryResultList;
import org.apache.stanbol.entityhub.servicesapi.site.ReferencedSite;
import org.apache.stanbol.entityhub.servicesapi.site.ReferencedSiteException;
import org.apache.stanbol.entityhub.servicesapi.site.SiteConfiguration;
import org.osgi.framework.BundleContext;

public final class ReferencedSiteSearcher extends TrackingEntitySearcher<ReferencedSite> implements EntitySearcher {
    
    private final String siteId;

    public ReferencedSiteSearcher(BundleContext context,String siteId) {
        super(context, ReferencedSite.class, 
            Collections.singletonMap(SiteConfiguration.ID,siteId));
        this.siteId = siteId;
    }
    
    @Override
    public Representation get(String id,Set<String> includeFields) {
        if(id == null || id.isEmpty()){
            return null;
        }
        Entity entity;
        ReferencedSite site = getSearchService();
        if(site == null){
            throw new IllegalStateException("ReferencedSite "+siteId+" is currently not available");
        }
        try {
            entity = site.getEntity(id);
        }  catch (ReferencedSiteException e) {
            throw new IllegalStateException("Exception while getting "+id+
                " from the ReferencedSite "+site.getId(),e);
        }
        return entity == null ? null : entity.getRepresentation();
    }

    @Override
    public Collection<? extends Representation> lookup(String field,
                                           Set<String> includeFields,
                                           List<String> search,
                                           String... languages) throws IllegalStateException {
        //build the query and than return the result
        ReferencedSite site = getSearchService();
        if(site == null){
            throw new IllegalStateException("ReferencedSite "+siteId+" is currently not available");
        }
        FieldQuery query = EntitySearcherUtils.createFieldQuery(site.getQueryFactory(), 
            field, includeFields, search, languages);
        QueryResultList<Representation> results;
        try {
            results = site.find(query);
        } catch (ReferencedSiteException e) {
            throw new IllegalStateException("Exception while searchign for "+
                search+'@'+Arrays.toString(languages)+"in the ReferencedSite "+
                site.getId(), e);
        }
        return results.results();
    }

    @Override
    public boolean supportsOfflineMode() {
        ReferencedSite site = getSearchService();
        //Do not throw an exception here if the site is not available. Just return false
        return site == null ? false : site.supportsLocalMode();
    }
}
