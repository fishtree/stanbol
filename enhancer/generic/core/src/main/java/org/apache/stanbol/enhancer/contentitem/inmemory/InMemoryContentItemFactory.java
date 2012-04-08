package org.apache.stanbol.enhancer.contentitem.inmemory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.enhancer.servicesapi.Blob;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.ContentItemFactory;
import org.apache.stanbol.enhancer.servicesapi.ContentSink;
import org.apache.stanbol.enhancer.servicesapi.ContentSource;
import org.apache.stanbol.enhancer.servicesapi.helper.ContentItemHelper;
import org.apache.stanbol.enhancer.servicesapi.impl.AbstractContentItemFactory;
import org.osgi.framework.Constants;
@Component(inherit=true)
@Service(value=ContentItemFactory.class)
@Properties(value={
    //set service ranking to an positive integer so that others do not accitently
    //override the default
    @Property(name=Constants.SERVICE_RANKING,intValue=100)
})
public class InMemoryContentItemFactory extends AbstractContentItemFactory implements ContentItemFactory {
    
    private static InMemoryContentItemFactory instance;
    /**
     * Getter for the singleton instance of this factory. Within an OSGI 
     * environment this should not be used as this Factory is also registered
     * as OSGI service.
     * @return the singleton instance
     */
    public static InMemoryContentItemFactory getInstance(){
        if(instance == null){
            instance = new InMemoryContentItemFactory();
        }
        return instance;
    }
    
    public InMemoryContentItemFactory() {
        super(true); //lazy initialisation makes a lot of sense for in-memory implementations
    }

    @Override
    protected ContentItem createContentItem(UriRef id, Blob blob, MGraph metadata) {
        return new InMemoryContentItem(id, blob, metadata);
    }
    
    @Override
    protected ContentItem createContentItem(String prefix, Blob blob, MGraph metadata) {
        return new InMemoryContentItem(ContentItemHelper.makeDefaultUri(prefix, blob), blob, metadata);
    }

    @Override
    public Blob createBlob(ContentSource source) throws IOException {
        if(source == null){
            throw new IllegalArgumentException("The parsed ContentSource MUST NOT be NULL!");
        }
        //use source.getData to avoid making copies of byte arrays
        return new InMemoryBlob(source.getData(), source.getMediaType(),null);
    }

    @Override
    public ContentSink createContentSink(String mediaType) throws IOException {
        return new InMemoryContentSink(mediaType);
    }

    
    protected static class InMemoryContentSink implements ContentSink {

        private final ByteArrayOutputStream out;
        private final InMemoryBlob blob;
        
        protected InMemoryContentSink(String mt){
            out = new ByteArrayOutputStream();
            blob = new InMemoryBlob(out, mt, null);
        }
        
        @Override
        public OutputStream getOutputStream() {
            return out;
        }

        @Override
        public Blob getBlob() {
            return blob;
        }
        
    }
}
