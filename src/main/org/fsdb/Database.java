package main.org.fsdb;

public class Database {


   public boolean create(String name){
      return FileSystem.createDir(name);
   }
}
