package bizsocket.core.MyCode;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by dxjf on 16/11/8.
 */
public class RequestContextQueue extends CopyOnWriteArrayList<RequestContext> {
    @Override
    public boolean remove(Object o) {
        if (o != null) {
            RequestContext con = (RequestContext) o;
            beforeRemove(con);
        }
        return super.remove(o);
    }

    @Override
    public RequestContext remove(int index) {
        RequestContext requestContext = super.remove(index);
        beforeRemove(requestContext);
        return requestContext;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Collection<RequestContext> requestContexts = (Collection<RequestContext>) c;
        for (RequestContext requestContext : requestContexts) {
            beforeRemove(requestContext);
        }
        return super.removeAll(c);
    }

    @Override
    public void clear() {
        for (RequestContext requestContext : this) {
            beforeRemove(requestContext);
        }
        super.clear();
    }

    @Override
    public boolean add(RequestContext requestContext) {
        beforeAdd(requestContext);
        return super.add(requestContext);
    }

    @Override
    public void add(int index, RequestContext element) {
        beforeAdd(element);
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends RequestContext> c) {
        for (RequestContext requestContext : c) {
            beforeAdd(requestContext);
        }
        return super.addAll(c);
    }

    private void beforeAdd(RequestContext requestContext) {
        if (requestContext != null) {
            if (!contains(requestContext)) {
                requestContext.onAddQueue();
            }
        }
    }

    private void beforeRemove(RequestContext con) {
        if (con != null) {
            con.onRemoveFromQueue();
        }
    }
}
