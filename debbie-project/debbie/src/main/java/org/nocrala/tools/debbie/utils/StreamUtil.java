package org.nocrala.tools.debbie.utils;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtil {

  public static <T> Stream<T> asStream(final Iterator<T> sourceIterator) {
    return asStream(sourceIterator, false);
  }

  public static <T> Stream<T> asStream(final Iterator<T> sourceIterator, final boolean parallel) {
    Iterable<T> iterable = () -> sourceIterator;
    return StreamSupport.stream(iterable.spliterator(), parallel);
  }

}
