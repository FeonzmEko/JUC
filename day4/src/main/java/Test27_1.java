public class Test27_1 {
    public static void main(String[] args) {
        WaitNotify_1 waitNotify = new WaitNotify_1(1,5);
        new Thread(()->waitNotify.print("a",1,2)).start();
        new Thread(()->waitNotify.print("b",2,3)).start();
        new Thread(()->waitNotify.print("c",3,1)).start();
    }
}

class WaitNotify_1 {
    private int flag;
    private int loopNumber;

    public WaitNotify_1(int flag, int loopNumber){
        this.flag = flag;
        this.loopNumber = loopNumber;
        System.out.println();
    }

    public void print(String s,int currentFlag,int nextFlag){
        for (int i = 0; i < loopNumber; i++) {
            synchronized (this){
                while(flag!=currentFlag){
                    try{
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.print(s);
                flag = nextFlag;
                this.notifyAll();
            }
        }
    }
}