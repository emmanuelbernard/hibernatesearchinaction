package com.manning.hsia.dvdstore;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.hibernate.search.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Indexed
@Analyzer(impl = StandardAnalyzer.class)
@ClassBridge(name = "",
             index = Index.TOKENIZED,
             store = Store.YES,
             impl = DOMExampleBridge.class)
public class CDDOM {

   private Integer id;
   private String title;
   private String artist;
   private String priceData;

   @Id
   @DocumentId
   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   @Field(index=Index.TOKENIZED, store=Store.YES)
   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   @Field(index=Index.TOKENIZED, store=Store.YES)
   public String getArtist() {
      return artist;
   }

   public void setArtist(String artist) {
      this.artist = artist;
   }

   public String getPriceData() {
      return priceData;
   }

   public void setPriceData(String priceData) {
      this.priceData = priceData;
   }
}