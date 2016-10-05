package com.dx168.bizsocket.core;

import com.dx168.bizsocket.core.RequestContext;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by tong on 16/10/5.
 */
public class RequestContextQuoue extends CopyOnWriteArrayList<RequestContext> {
    @Override
    public boolean remove(Object o) {
        boolean result =  super.remove(o);
        if (result && o != null) {
            RequestContext requestContext = (RequestContext) o;
            requestContext.onRemoveFromQuoue();
        }
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Collection<RequestContext> collection = (Collection<RequestContext>) c;
        for (RequestContext context : collection) {
            context.onRemoveFromQuoue();
        }
        return super.removeAll(c);
    }

    @Override
    public boolean add(RequestContext requestContext) {
        if (requestContext != null) {
            requestContext.onAddToQuote();
        }
        return super.add(requestContext);
    }
}
