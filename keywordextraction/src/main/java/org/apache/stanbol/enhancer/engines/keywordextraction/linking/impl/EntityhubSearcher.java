package org.apache.stanbol.enhancer.engines.keywordextraction.linking.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.stanbol.enhancer.engines.keywordextraction.linking.EntitySearcher;
import org.apache.stanbol.entityhub.servicesapi.Entityhub;
import org.apache.stanbol.entityhub.servicesapi.EntityhubException;
import org.apache.stanbol.entityhub.servicesapi.model.Entity;
import org.apache.stanbol.entityhub.servicesapi.model.Representation;
import org.apache.stanbol.entityhub.servicesapi.query.FieldQuery;
import org.apache.stanbol.entityhub.servicesapi.query.QueryResultList;
import org.osgi.framework.BundleContext;

public final class EntityhubSearcher extends TrackingEntitySearcher<Entityhub> implements EntitySearcher {
    
    public EntityhubSearcher(BundleContext context) {
        super(context,Entityhub.class,null);
    }
    
    @Override
    public Representation get(String id,Set<String> includeFields) {
        if(id == null || id.isEmpty()){
            return null;
        }
        Entityhub entityhub = getSearchService();
        if(entityhub == null){
            throw new IllegalStateException("The Entityhub is currently not active");
        }
        Entity entity;
        try {
            entity = entityhub.getEntity(id);
        }  catch (EntityhubException e) {
            throw new IllegalStateException("Exception while getting "+id+
                " from the Entityhub",e);
        }
        return entity == null ? null : entity.getRepresentation();
    }
    @Override
    public Collection<? extends Representation> lookup(String field,
                                           Set<String> includeFields,
                                           List<String> search,
                                           String... languages) throws IllegalStateException {
        Entityhub entityhub = getSearchService();
        if(entityhub == null){
            throw new IllegalStateException("The Entityhub is currently not active");
        }
        FieldQuery query = EntitySearcherUtils.createFieldQuery(entityhub.getQueryFactory(),
            field, includeFields, search, languages);
        QueryResultList<Representation> results;
        try {
            results = entityhub.find(query);
        } catch (EntityhubException e) {
            throw new IllegalStateException("Exception while searchign for "+
                search+'@'+Arrays.toString(languages)+"in the Entityhub", e);
        }
        return results.results();
    }

    @Override
    public boolean supportsOfflineMode() {
        return true; //the entityhub is always offline
    }

}
