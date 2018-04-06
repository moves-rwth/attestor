public class SLList {
    public SLList next;

    public SLList( SLList next ){
         this.next = next;
    }

    public static SLList prependSLList( SLList tail ){
         SLList first = new SLList( tail );
         SLList curr = first;
         for( int i = 0; i < 10; i++ ){ //for Attestor this is non-deterministic
               SLList tmp = new SLList( null ); //for demonstration: setting next to null first
               curr.next = tmp;
               curr = tmp;
         }
         return first;
    }
    
    
    // this is just a dummy such that the file compiles
    public static void main(String[] args) {
        
        prependSLList( null );
    }
} 
