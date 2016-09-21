package com.kaldi.reco;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


// Plain old Java Object it does not extend as class or implements 
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation. 
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML. 

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /kaldi


@Path("/kaldi")
public class kaldi {
	
	private String kaldi_path="/home/kaldi/kaldi";
    public static String data_path="/home/kaldi/kaldi/egs/villes/data";
    public static String reco_path="/home/kaldi/kaldi/egs/villes/exp";
	
	
	// This method is called if TEXT_PLAIN is request
	  @GET
	  @Produces(MediaType.TEXT_PLAIN)
	  public static String new_reco_text(@QueryParam("fichier")  String fichier) throws FileNotFoundException, IOException, InterruptedException{
		   
			int[] i={0};
			String[] res={"","",""};
			affiche(reco_text(fichier),i,res);
			if(i[0]<3){
				  Process p;
				    p = Runtime.getRuntime().exec("/home/kaldi/kaldi/egs/villes/get_nbest.sh /home/kaldi/kaldi/egs/villes/nbest/ /home/kaldi/kaldi/egs/villes/exp/tri3b_new/decode.si 0.1 "+fichier);
				    p.waitFor();
				nbest(i,res,fichier);}
			return res[0]+" "+res[1]+" "+res[2];
		   
	   }
	  
	 
	  public static String reco_text (String fichier) throws FileNotFoundException, IOException, InterruptedException{
		converter(fichier); 
		remove(fichier);
		 //octaveTest(fichier);
		  fichiers(fichier);
		  script();
		  String r=results("tri3b");
		  return (r);
		    }

	  // This method is called if XML is request
	  @GET
	  @Produces(MediaType.TEXT_XML)
	  public static String reco_xml (@QueryParam("fichier") String fichier) throws FileNotFoundException, IOException, InterruptedException{
		    fichiers(fichier);
		    script();
		    return "<?xml version=\"1.0\"?>"
			        + "\n<resultat>" +results("tri3b")+"</resultat>\n";
		    }
			

	  // This method is called if HTML is request
//	  @GET
//	  @Produces(MediaType.TEXT_HTML)
//	  public static String result2(@QueryParam("fichier") String fichier) throws IOException, InterruptedException{
//			StringBuffer output = new StringBuffer();
//			Process p;
//		    p = Runtime.getRuntime().exec("java -jar /home/kaldi/kaldi.jar /home/kaldi/reco/"+fichier);
//		    p.waitFor();
//		    BufferedReader reader = 
//	        new BufferedReader(new InputStreamReader(p.getInputStream()));
//	        String line = "";			
//	        while ((line = reader.readLine())!= null) {
//		    output.append(line + "\n");
//	        }
//			return "<html> " + "<title>" + "Kaldi" + "</title>"
//			        + "<body><p>" + output.toString()+"</p>" + "</p></body>" + "</html> ";	
// 
//		}
	  public String sayHtmlHello() {
	    return "<html> " + "<title>" + "Hello Jersey" + "</title>"
	        + "<body><h1>" + "Hello Jersey" + "</body></h1>" + "</html> ";
	  }
	  
	  
	  
	    // éxecute la reconnaissance, il écrit dans un fichier log les résultats obtenu par
	    // tout les reconnaisseurs, et affiche le résultat obtenu par le reconnaisseur tri3b
	  @GET
	  @Produces(MediaType.TEXT_HTML)
	  public static String reco (@QueryParam("fichier") String fichier) throws FileNotFoundException, IOException, InterruptedException{
	    fichiers(fichier);
	    script();
	    return "<html><head><title>Kaldi - Raconnaissance vocale en Darija</title></head>"
	    		+ "<h1>"+results("tri3b")+"</h1></html>";
	    }
	  
	  // Permet d'exécuter le script de la reconnaissance
	  public static void script() throws IOException, InterruptedException{
	    Process p;
	    p = Runtime.getRuntime().exec("/home/kaldi/kaldi/egs/villes/reco.sh");
	    p.waitFor();
	  }
	  
	 // cette fonction permet de lire le résultat de la reconnaissance, il prend en paramètre
	 // le nom du reconnaisseur utilisé. Les valeurs possibles sont : (mono,tri1,tri2a,tri2b,tri3b,tri4a,tri4b)
	  public static String results(String reco) throws FileNotFoundException{
	      File f,f2;
	      Scanner s;
	      //f=new File(reco_path+"/"+reco+"_test2/decode/log/decode.1.log");
	     // s=new Scanner(f);
	     // for(int i=0; i<13; i++)
	     // s.nextLine();
	      //String h= s.nextLine().substring(5);
	      f2=new File(reco_path+"/"+reco+"_new/decode.si/log/decode.1.log");
	       s=new Scanner(f2);
	       for(int i=0; i<10; i++)
	       s.nextLine();
return s.nextLine().substring(5);
	      }
	  
	  
	  //création des fichiers textes requis par Kaldi pour faire la reconaissance
	  public static void fichiers(String chemin_wav) throws FileNotFoundException{
		  chemin_wav="/home/kaldi/reco/conv/"+chemin_wav;//conv
	      PrintWriter w_wav=new PrintWriter(data_path+"/reco/wav.scp");
	      w_wav.println("file "+chemin_wav);
	      w_wav.close();
	  }
	  
	  //converti le fichier gsm en pcm
	  public static void converter(String nom) throws IOException, InterruptedException{
		  Process p;
		    p = Runtime.getRuntime().exec("sox /home/kaldi/reco/"+nom+" -e signed-integer /home/kaldi/reco/conv/"+nom);
		    p.waitFor();
			
	  }
	  // supprime le fichier gsm
	  public static void remove(String nom) throws IOException, InterruptedException{
		  Process p;
		    p = Runtime.getRuntime().exec("rm /home/kaldi/reco/"+nom);
		    p.waitFor();
			
	  }
	  
	  //exécution du vad
		public static void octaveTest(String fich) throws IOException, InterruptedException{
			Process p;
		  p = Runtime.getRuntime().exec("/home/kaldi/reco/conv/remove_silence.m /home/kaldi/reco/conv/"+fich);//conv
		    p.waitFor();
//		    StringBuffer output = new StringBuffer();
//		    BufferedReader reader = 
//	                new BufferedReader(new InputStreamReader(p.getInputStream()));
//
//	            String line = "";			
//	while ((line = reader.readLine())!= null) {
//		output.append(line + "\n");
//	}
//	return output.toString();
		} 
	  
		//functions used by the new version
		public static void affiche(String r,int[] i,String[] res) throws FileNotFoundException{
			  if(r!=""){
			  StringTokenizer st=new StringTokenizer(r);
			  while(st.hasMoreTokens()){
				 String t=st.nextToken();
				 if(i[0]==1){if(!t.contentEquals(res[0])){res[i[0]]=t; i[0]=i[0]+1;}}
				 if(i[0]==2){if(!t.contentEquals(res[0]) && !t.contentEquals(res[1])){res[i[0]]=t; i[0]=i[0]+1;}}
				 if(i[0]==0){res[i[0]]=t; i[0]=i[0]+1;}  
			  }}
			 // System.out.println("\n1: "+res[0]+"\n"+"2: "+res[1]+"\n3: "+res[2]+"\n taille: "+i[0]);
			  }
		  
			  
		  public static void nbest(int[] i,String[] res,String fichier) throws FileNotFoundException{
			  File f=new File("/home/kaldi/kaldi/egs/villes/nbest/"+fichier+".all.nbest.roman");
			  Scanner sc=new Scanner(f);
			  while(i[0]!=3){
				  String s=sc.nextLine();
				  Scanner sc2=new Scanner(s);
				  sc2.next();
				  s=sc2.nextLine();
				  affiche(s,i,res);
				  
				  }

}
}
