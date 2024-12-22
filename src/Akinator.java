
import javafx.application.Application;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class Akinator {
	private String question;
	private int response;
	private String guess;
	private List<Integer> ResponseBuilder;
	private Path pathPerso;
	private Path pathQuest;
	
	Akinator(String question, int response, String guess) {
		this.question = question;
		this.response = response;
		this.guess = guess;
		this.ResponseBuilder = new ArrayList<Integer>();
		this.pathPerso = Paths.get("./personnages.txt");
		this.pathQuest = Paths.get("./questions.txt");
	}
	
	public String getQuestion(int i) {
		
		try {
			List<String> lines = Files.readAllLines(pathQuest);
			question = lines.get(i);
		}
		catch(IOException e) {
	        e.printStackTrace();
			
		}
		return question;
	}

	public int getResponse() {
        Scanner scanner = new Scanner(System.in);
        response = scanner.nextInt();
        ResponseBuilder.add(response);

		return response;
	}
	
	public String AddPersonnage() {
		System.out.println("Vous m'avez posé une colle, à qui vous pensiez ?\n");
		Scanner scanner = new Scanner(System.in);
        String personnage = scanner.nextLine();
		try(BufferedWriter writeNew = Files.newBufferedWriter(pathPerso, StandardOpenOption.APPEND)){			
			writeNew.write(personnage);
			writeNew.newLine();
			StringBuilder StrCode = new StringBuilder();
			for(int i = 0; i < ResponseBuilder.size(); i++) {
				StrCode.append(ResponseBuilder.get(i));
			}
			writeNew.write(StrCode.toString());
			writeNew.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Votre personnage a bien été ajouté";
	}
	
	public void prepareTrainingData() {
		List<String> data = new ArrayList<>();
		try {
	        List<String> perso = Files.readAllLines(pathPerso);
	        
	        for (int i = 0; i < perso.size(); i += 2) {
	            String personnage = perso.get(i);  // Nom du personnage
	            String codes = perso.get(i + 1);  // Les réponses sous forme de chaîne
	            data.add(personnage + ":" + codes);  // Une paire {réponse, personnage}
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public String FinalGuess() {
		List<String> personnages = new ArrayList<String>();
		List<String> codes = new ArrayList<String>();
		List<String> candidates = new ArrayList<String>();
		
		try {
			List<String> perso = Files.readAllLines(pathPerso);
			
			for(int i = 0; i < perso.size(); i++) {
				if(i % 2 == 0) {
					personnages.add(perso.get(i));
				}
				else {
					codes.add(perso.get(i));
				}
			}
			candidates.addAll(personnages);
	
			for(int i = 0; i < codes.size(); i++) {
				
				for(int j = 0; j < ResponseBuilder.size(); j++){
					char codeChar = codes.get(i).charAt(j);
					int response = ResponseBuilder.get(j);
					
					if(codeChar != Character.forDigit(response, 10)) {
						candidates.remove(personnages.get(i));
						break;
					}
				}
			}
		}
		catch(IOException e) {
	        e.printStackTrace();
			
		}
		if(candidates.size() == 1) {
			guess = "je pense à " + candidates.get(0);
		}
		else if(candidates.size() > 1) {
			guess = "Plusieurs personnages ont été trouvés"; 
		}
		else if(candidates.size() < 1) {
			guess = AddPersonnage() ;
		}
		
		return guess;
	}
	
	public boolean PlayAgain() {
		System.out.println("On rejoue ?");
		Scanner scanner = new Scanner(System.in);
        String choix = scanner.nextLine();
        boolean decision = false;
        if(choix.toLowerCase().equals("oui")) {
        	decision = true; 
        }
		if(choix.toLowerCase().equals("non")) {
		   decision = false;   	
		}
        		
		return decision;
	}


	public static void main(String[] args) {
		boolean replay = true;
		while(replay) {
			Akinator akinator = new Akinator("", 0, "");
			
			for(int i = 0; i < 8; i++) {	
				System.out.println(akinator.getQuestion(i));
				System.out.println("1 - Oui, 5 - Non");
				
				akinator.getResponse();
			}
			System.out.println(akinator.FinalGuess());
			replay = akinator.PlayAgain();
			
			if(!replay){
				System.out.println("Bon vent !");
			}			
		}
		
	}

}
