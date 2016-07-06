package com.jprarama.newsfeedapp.consumer;

import java.util.ArrayList;

/**
 * Created by joshua on 5/7/16.
 */
public interface ListConsumer<T> extends ExceptionConsumer {

    void consume(ArrayList<T> list);
}
