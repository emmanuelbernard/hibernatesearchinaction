package com.manning.hsia.test.ch12;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.store.Directory;
import org.hibernate.HibernateException;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.search.event.FullTextIndexEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public abstract class SearchTestCase extends HSiATestCase {

   private static final Logger log = LoggerFactory.getLogger( SearchTestCase.class);
   private static File indexDir;

   static {
      File current = getAbsoluteDir();
      indexDir = new File(current, "indextemp");
      log.debug("Using {} as index directory.", indexDir.getAbsolutePath());
   }

   @SuppressWarnings("unchecked")
   protected Directory getDirectory(Class clazz) {
      return getLuceneEventListener().getSearchFactoryImplementor().getDirectoryProviders(clazz)[0].getDirectory();
   }

   protected void delete(File sub) {
      if (sub.isDirectory()) {
         for (File file : sub.listFiles()) {
            delete(file);
         }
         sub.delete();
      } else {
         sub.delete();
      }
   }

   private FullTextIndexEventListener getLuceneEventListener() {
      PostInsertEventListener[] listeners = ((SessionFactoryImpl) getSessions()).getEventListeners().getPostInsertEventListeners();
      FullTextIndexEventListener listener = null;
      for (PostInsertEventListener candidate : listeners) {
         if (candidate instanceof FullTextIndexEventListener) {
            listener = (FullTextIndexEventListener) candidate;
            break;
         }
      }
      if (listener == null) throw new HibernateException("Lucene event listener not initialized");
      return listener;
   }

   protected File locateBaseDir() {
      return indexDir;
   }

   protected static File getAbsoluteDir() {
      File file = new File("dvd_index");
      String absPath = file.getAbsolutePath();

      return new File(absPath);
   }

   protected void copyIndexes() throws IOException {
      File productSrcDir = new File(new File("dvd_index_backup/com.manning.hsia.test.Product").getAbsolutePath());
      File productDestDir = new File(locateBaseDir().getAbsolutePath() + "/com.manning.hsia.test.Product");

      FileUtils.copyDirectory(productSrcDir, productDestDir);
   }
}