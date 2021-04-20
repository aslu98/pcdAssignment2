package part1.jpf;

public class Main {
	public static void main(String[] args) {
		try {
			
			View view = new View();

			Controller controller = new Controller(view);
			view.addListener(controller);
			
			view.display();						
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		//System.out.println("hello");
	}
}
