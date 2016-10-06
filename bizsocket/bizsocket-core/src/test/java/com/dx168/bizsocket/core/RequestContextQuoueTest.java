package com.dx168.bizsocket.core;

import junit.framework.TestCase;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by tong on 16/10/6.
 */
public class RequestContextQuoueTest extends TestCase {
    final RequestContextQuoue requestContextQuoue = new RequestContextQuoue();
    boolean isInvokeOnAddToQuote;
    boolean isInvokeOnRemoveFromQuoue;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        requestContextQuoue.clear();
        isInvokeOnAddToQuote = false;
        isInvokeOnRemoveFromQuoue = false;
    }

    @Test
    public void testConcurrent() {
        for (int i = 0; i < 40; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 20; j++) {
                        int i = new Random().nextInt(10);
                        if (i == 1) {
                            if (requestContextQuoue.size() > 0) {
                                int position = new Random().nextInt(requestContextQuoue.size());
                                if (position < 0) {
                                    position = 0;
                                }
                                if (position >= requestContextQuoue.size()) {
                                    position = requestContextQuoue.size() - 1;
                                }
                                requestContextQuoue.remove(position);
                            }
                        }
                        else if (i == 2) {
                            for (RequestContext context : requestContextQuoue) {
                                System.out.println(context);
                            }
                        }
                        else if (i == 3) {
                            requestContextQuoue.clear();
                        }
                        else {
                            requestContextQuoue.add(new RequestContext());
                        }
                    }
                }
            }).start();
        }
    }

    @Test
    public void testAdd() throws Exception {
        final RequestContext requestContext = new RequestContext(){
            @Override
            public void onAddToQuote() {
                isInvokeOnAddToQuote = true;
                super.onAddToQuote();
            }
        };

        requestContextQuoue.add(requestContext);
        assertEquals(isInvokeOnAddToQuote,true);
        requestContextQuoue.clear();

        requestContextQuoue.add(0,requestContext);
        assertEquals(isInvokeOnAddToQuote,true);
        requestContextQuoue.clear();

        requestContextQuoue.addAll(new ArrayList<RequestContext>(){{add(requestContext);}});
        assertEquals(isInvokeOnAddToQuote, true);
    }

    @Test
    public void testRemove() throws Exception {
        final RequestContext requestContext = new RequestContext(){
            @Override
            public void onRemoveFromQuoue() {
                isInvokeOnRemoveFromQuoue = true;
                super.onRemoveFromQuoue();
            }
        };

        requestContextQuoue.add(requestContext);
        assertEquals(isInvokeOnRemoveFromQuoue,false);
        requestContextQuoue.remove(requestContext);
        assertEquals(isInvokeOnRemoveFromQuoue,true);

        isInvokeOnRemoveFromQuoue = false;
        requestContextQuoue.add(requestContext);
        assertEquals(isInvokeOnRemoveFromQuoue,false);
        requestContextQuoue.remove(0);
        assertEquals(isInvokeOnRemoveFromQuoue,true);

        isInvokeOnRemoveFromQuoue = false;
        requestContextQuoue.add(requestContext);
        assertEquals(isInvokeOnRemoveFromQuoue,false);
        requestContextQuoue.removeAll(new ArrayList<Object>(){{add(requestContext);}});
        assertEquals(isInvokeOnRemoveFromQuoue, true);

        isInvokeOnRemoveFromQuoue = false;
        requestContextQuoue.add(requestContext);
        assertEquals(isInvokeOnRemoveFromQuoue,false);
        requestContextQuoue.clear();
        assertEquals(isInvokeOnRemoveFromQuoue,true);
    }
}
