package main.org.fsdb;

public class Database {
    public QueryResult executeQuery(Query query) {
        QueryResult result = new QueryResult();

        result.successful = true;
        result.action = query.action;

        // doesn't actually do anything at the moment
        result.value = query.toString();

        return result;
    }


   public boolean create(String name){
      return FileSystem.createDir(name);
   }
}
