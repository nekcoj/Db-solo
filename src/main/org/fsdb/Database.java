package main.org.fsdb;

import java.io.File;

public class Database {


   public boolean create(String name){
      return new File(name).mkdir();
   }
}
