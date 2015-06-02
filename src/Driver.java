import java.util.Scanner;

public class Driver {

	
	public static void main(String[] args)
	{
		Dictionary d = new Dictionary("words_ospd.txt");
		
		System.out.println("Welcome to my spell checker and suggester!");
		System.out.println("Test it out by inputting words below, to quit submit an empty string");
		
		Scanner scan = new Scanner(System.in);
		String input = "input";
		
		while(!input.equals("")) {
			System.out.print("> ");	
			input = scan.nextLine();
			if(d.check(input)) {
				System.out.println("\"" + input + "\" is valid");
			} else {
				System.out.print("\"" + input + "\" was not found in the dictionary");
				String results[] = d.getLikelySuggestions(input, 10);
				if(results.length == 0) {
					System.out.println(", no similar words were found");					
				} else {
					System.out.println(", did you mean: ");
					for(String x : results) {
						System.out.println("\t\"" + x + "\"");
					}
				}
			}
		}
		
		scan.close();
	}
}