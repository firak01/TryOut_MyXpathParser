package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/* Aus dem TryOut Projekt übernommener Parser, 
 * TODO: Änderungen dorthin übernehmen.*/
public class MyXPathParserFGL {		
	String sBaseDirectory = new String("");
	public String getBaseDirectory() {
		return sBaseDirectory;
	}

	public void setBaseDirectory(String sBaseDirectory) {
		this.sBaseDirectory = sBaseDirectory;
	}

	
	String sFileName = new String("");
	public String getFileName() {
		return sFileName;
	}

	public void setFileName(String sFileName) {
		this.sFileName = sFileName;
	}
	
	String sXPathExpressionCurrent = new String("");
	public String getXPathExpressionCurrent() {
		return sXPathExpressionCurrent;
	}
	public void setXPathExpressionCurrent(String sXPathExpressionCurrent) {
		this.sXPathExpressionCurrent = sXPathExpressionCurrent;
	}


		XPath xPath=null;
		public XPath getXPath() {
			if(xPath==null){
				xPath = XPathFactory.newInstance().newXPath();
			}
			return xPath;
		}

		public void setXPath(XPath xPath) {
			this.xPath = xPath;
		}
		
		
		DocumentBuilderFactory builderFactory=null;
		public DocumentBuilderFactory getBuilderFactory() {
			if(builderFactory==null){
				builderFactory = DocumentBuilderFactory.newInstance();
			}
			return builderFactory;
		}

		public void setBuilderFactory(DocumentBuilderFactory builderFactory) {
			this.builderFactory = builderFactory;
		}
		
				
		DocumentBuilder builder = null;
		public DocumentBuilder getBuilder() {
			if(builder==null){
				try{
					DocumentBuilderFactory builderFactory = this.getBuilderFactory();
					builder = builderFactory.newDocumentBuilder();
				}catch(ParserConfigurationException e){
					e.printStackTrace();
				}
			}
			return builder;
		}

		public void setBuilder(DocumentBuilder builder) {
			this.builder = builder;
		}

	
		Document document = null;
		public Document getDocument() {
			if(document==null){
				//1. DocumentBuilder Objekt
				DocumentBuilder builder = this.getBuilder(); 
				String sBaseDirectory = this.getBaseDirectory();
				String sFileName = this.getFileName();
				
				//2. Document Objekt
				System.out.println("Verarbeite Dokument: '" + sBaseDirectory + File.separator + sFileName + "'");								
				try {
					document = builder.parse(new FileInputStream(sBaseDirectory + File.separator + sFileName));
					this.setDocument(document);
					
					//Alternative: XML String parsen, wäre so:
					//String sXml = "....";
					//document = builder.parse(new ByteArrayInputStream(xml.getBytes()));
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}								
			}
			return document;
		}

		public void setDocument(Document document) {
			this.document = document;
		}
		
		
		
		//######################################################################

		

		public void MyXPathPaserFGL(){			
		}
		
		public boolean startIt(String[] args) {
			boolean bReturn = false;
			
			main:{
			try{	
				System.out.println("################################################");
				System.out.println("###       MyXPathParserFGL - neuer Lauf      ###");
				
				//Parameterübergabe (z.B. für ein gepacktes .jar - File
				String stemp = "Kein Verzeichnis mit den zur parsenden Dateien als Parameter angegeben";
				if(args==null) throw new Exception(stemp + " (args null).");
				if(args.length==0) throw new Exception(stemp + " (0).");
				if(args[0].equals("") || args[0]==null) throw new Exception(stemp + " (null)");
				
				stemp = "Keine zu parsende Datei angeben";
				if(args.length==1) throw new Exception(stemp + " (1).");
				if(args[1].equals("") || args[1]==null) throw new Exception(stemp + " (null)");
				
				stemp = "Kein beschreibender Ausdruck für das Ziel der Verarbeitung angegeben";
				if(args.length==2) throw new Exception(stemp + " (2).");
				if(args[2].equals("") || args[2]==null) throw new Exception(stemp + " (null)");
				
				stemp = "Keine XPath Ausdruck angeben";
				if(args.length==3) throw new Exception(stemp + " (3).");
				if(args[3].equals("") || args[3]==null) throw new Exception(stemp + " (null)");
				
				//Die Verzeichnisse dürfen maximal mit einem Leerzeichen versehen sein!				
				String sExpressionDesc = new String("");				
				ArrayList<String> listasXPathExpression = new ArrayList();
				int iArgCount = -1;
				for(String s : args){
					iArgCount++;
					switch (iArgCount){
					case 0:
						this.setBaseDirectory(s);					
						break;
					case 1:
						this.setFileName(s);
						break;
					case 2:
						sExpressionDesc = s;
						break;
					default:					
						listasXPathExpression.add(s);
						break;						
					}					
				}
			
			//########################################################
			//+++ Zuerst ein DOM-Document
			Document document = this.getDocument();
			
			//3. Array aus den herausgefilterten Übergabeparametern bauen
			String[] saExpression = new String[listasXPathExpression.size()];
			saExpression = listasXPathExpression.toArray(saExpression);
			
						
			//Ziel ist es zuerst über Lokalisierungen zu einem Knoten zu gelangen.
			//An der "letzen" Lokalisierung angekommen, wird dann auch eine NodeList geholt
			System.out.println("Ausgabeziel: " + sExpressionDesc);
			
			Node node=null;
			Node nodenew = null;		
			NodeList nodeList = null;
			for(String expression : saExpression){
				System.out.println("Expression: '" + expression + "'");
				this.setXPathExpressionCurrent(expression);		
												
				//FGL: Ein etwas (!) 'generischerer' Ansatz, d.h. von dem ausgwählten Ausdruck unabhängiger.
				//read an xml node using xpath
				//Beim 1. Durchlaufen der Schleife vom Dokument ausgehen.
				//Beim Weiteren Durchlaufen der Schleife von den zuvor lokalisierten Knoten ausgehen.
				if(node==null){
					System.out.println("Noch kein Node vorhanden. evaluiere vom Dokument aus.");
					nodenew = (Node) this.getXPath().compile(expression).evaluate(document, XPathConstants.NODE);
				}else{										
					//read an nodelist using xpath
					System.out.println("Node vorhanden. evaluiere von diesem Node aus.");
					nodeList = (NodeList) this.getXPath().compile(this.getXPathExpressionCurrent()).evaluate(node, XPathConstants.NODESET);					
				}
				node = nodenew;
				
				if(nodeList!=null){
					this.printNodeList(nodeList);
				}else{
					if(saExpression.length>=2){
						System.out.println("Suche nach der Knotenliste erst ab dem 2. übergebenen XPath Ausdruck.");				
					}else{
						System.out.println("Keine weiteren XPath-Ausdrücke übergeben. Gib sofort die Knotenliste aus.");
						//nodeList = (NodeList) this.getXPath().compile(this.getXPathExpressionCurrent()).evaluate(node, XPathConstants.NODESET);
						//Nein, gehe vom Dokument aus...
						nodeList = (NodeList) this.getXPath().compile(expression).evaluate(document, XPathConstants.NODESET);
						this.printNodeList(nodeList);					
					}
				}//end if nodeList!= null
			}//end for expression
			
			if(xPath==null){
				bReturn = true;
				break main;
			}
			
			

			
			
			//Wenn ein Stringwert zurückkommt, liefere diesen, ansonsten den Node-Wert (FGL-Erweiterung). Beachte die null-Überprüfung ist absichtlich so von der Reihenfolge her.		
//			if(null != node && null != node.getNodeValue()){
//				System.out.println("Node - Wert: '" + node.getNodeValue() + "'");
//			}else{
//				
//				//read a String Value
//				//Beim 1. Durchlaufen der Schleife vom Dokument ausgehen.
//				//Beim Weiteren Durchlaufen der Schleife von den zuvor lokalisierten Knoten ausgehen.
//				if(node==null){
//				String sValue = xPath.compile(expression).evaluate(document);
//				System.out.println("String - Wert: '" + sValue + "'" );
//				}else{
//					String sValue = xPath.compile(expression).evaluate(node);
//					System.out.println("String - Wert: '" + sValue + "'" );
//				}
//			}		
			
			//XPath xPath = XPathFactory.newInstance().newXPath();
//Wann ist das sinnvoll????					
//			if(node==null){
//			 nodeList = (NodeList) xPath.compile(sXPathExpression).evaluate(document, XPathConstants.NODESET);
//			}else{
//				nodeList = (NodeList) xPath.compile(sXPathExpression).evaluate(node, XPathConstants.NODESET);
//			}
//			for(int i = 0 ; i < nodeList.getLength(); i++){
//				System.out.println(i+1 + ". Wert, erster Kindknoten: '" + nodeList.item(i).getFirstChild().getNodeValue() + "'");
//			}
			
			//TODO GOON Diese NodeList Verarbeitung wurde noch oben in die Schleife genommen.
			//          Was passiert, wenn es nur 1 XPath - Ausdruck gibt???
//			if(node!=null){
//				nodeList = node.getChildNodes();
//				icounter=0;
//					for(int i = 0 ; nodeList != null && i < nodeList.getLength(); i++){					
//						Node nodeSub = nodeList.item(i);
//						if(nodeSub.getNodeType() == Node.ELEMENT_NODE){
//							icounter++;
//							System.out.println( icounter + ". Wert (" + i+1 + ". Knoten, Knotentname:Wert erster Kindknoten) | " + nodeList.item(i).getNodeName() + " : " + nodeSub.getFirstChild().getNodeValue());
//						}
//					}
//				}
				bReturn = true;								
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XPathExpressionException e) {				
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace(System.err);				
			}
		}//end main:
			return bReturn;
		}
		
		/*
		 * 
		 * //TODO: Das schreit nach Rekursion...
		 * //TODO: Abstand vom linken Rand erhöhen bei jeder weiteren Ebene.
		 * 
		 *  Merke: Die nodeList kann auch die Attributsliste eines Items sein.
		 */
		public boolean printNodeList(NodeList nodeList) throws XPathExpressionException{
			boolean bReturn = false;
			int icounter=0;
			Node nodeSub=null;
			int iLevel = 0;
			String sLine = new String("");
			for(int i = 0 ; i < nodeList.getLength(); i++){
				//System.out.println(i+1 + ". Unterknoten des Knotens: '" + nodeList.item(i).getFirstChild().getNodeValue() + "'");
				
				nodeSub = nodeList.item(i); //Merke: Das kann auch die Attributsliste eines Items sein.
				Short shNodeType = nodeSub.getNodeType();
				if(shNodeType==Node.ATTRIBUTE_NODE){										
					sLine = i+1 + ". Attributknoten (haben keinen ParentNode) Knotenname '" + nodeSub.getNodeName() + "'";
				}else{
					sLine = i+1 + ". Unterknoten des Knotens: '" + nodeSub.getParentNode().getNodeName() + "'";
				}
				System.out.println( MyXPathParserUtilFGL.computeLineLeveledString(sLine, iLevel));
				
				
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				if(nodeSub.getNodeType() == Node.ATTRIBUTE_NODE){
					//### Attributknoten: Attributname und Wert zurückliefern
					sLine = "Attributname : Wert | " + nodeSub.getNodeName() + ":" + nodeSub.getNodeValue();
					System.out.println( MyXPathParserUtilFGL.computeLineLeveledString(sLine, iLevel));
					
				}
				
														
				//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 			
				if(nodeSub.getNodeType() == Node.ELEMENT_NODE){
					//### Elementknoten: Namen oder Wert zurückliefern
					//Wenn ein Stringwert zurückkommt, liefere diesen, ansonsten den Node-Wert (FGL-Erweiterung). Beachte die null-Überprüfung ist absichtlich so von der Reihenfolge her.		
					if(null != nodeSub && null != nodeSub.getNodeValue()){					
						sLine = "NodeSub - '" + MyXPathParserUtilFGL.computeLineNodeValue(nodeSub) + "'";
						System.out.println( MyXPathParserUtilFGL.computeLineLeveledString(sLine, iLevel));
					}else{
						if(nodeSub==null){
							String sValue = xPath.compile(this.getXPathExpressionCurrent()).evaluate(document);
							sLine = "NodeSub-Teil-Document Evaluierung: String - Wert: '" + sValue + "'";
							System.out.println( MyXPathParserUtilFGL.computeLineLeveledString(sLine, iLevel));
						}else{								
							sLine = "NodeSub - '" + MyXPathParserUtilFGL.computeLineNode(nodeSub)+ "'";
							System.out.println( MyXPathParserUtilFGL.computeLineLeveledString(sLine, iLevel));
							if(nodeSub.getNodeType()!=Node.TEXT_NODE){
								sLine = "Kein Textknoten";
								System.out.println( MyXPathParserUtilFGL.computeLineLeveledString(sLine, iLevel));
							}else{
								sLine = "Wert: " + nodeSub.getNodeValue();
								System.out.println( MyXPathParserUtilFGL.computeLineLeveledString(sLine, iLevel));
								//read a String Value
								//Beim 1. Durchlaufen der Schleife vom Dokument ausgehen.
								//Beim Weiteren Durchlaufen der Schleife von den zuvor lokalisierten Knoten ausgehen.
								
								//TODO GOON: Ist diese erneute evaluierung nicht quatsch, man müsste eher nodeSub.getValue verwenden
								String sValue = xPath.compile(this.getXPathExpressionCurrent()).evaluate(nodeSub);
								
								//Das gibt dann den String wert aller unterknoten aus...	
								sLine = "NodeSub-Teil-NodeSub evaluierung: String - Wert: '" + sValue + "'";
								System.out.println( MyXPathParserUtilFGL.computeLineLeveledString(sLine, iLevel));
							}																
						}
					}		
					
					
					//### Elementknoten: Werte zurückliefern
					icounter++;
					iLevel = 1;
					//System.out.println( icounter + ". Wert (" + i+1 + ". Knoten," + MyXPathParserUtilFGL.computeLineNode(nodeSub, iLevel) + ")");
					sLine = icounter + ". Wert (" + i+1 + ". Knoten," + MyXPathParserUtilFGL.computeLineNode(nodeSub) + ")";
					System.out.println( MyXPathParserUtilFGL.computeLineLeveledString(sLine, iLevel));
					
					//TODO GOON: Hier neu die Nodeliste ALLER Unterknoten holen
					//           theoretisch wäre das dann der Fall für die nächste Expression.
					//           Zum Code siehe unten die Verarbeitung ausserhalb der Schleife.
					NodeList nodeSubList = nodeSub.getChildNodes();
					int icounterSub=0;							
					iLevel = 2;
					for(int iSub = 0 ; nodeSubList != null && iSub < nodeSubList.getLength()-1; iSub++){					
						Node nodeSubSub = nodeSubList.item(iSub);
						if(nodeSubSub.getNodeType() != Node.TEXT_NODE){
							icounterSub++;
							sLine =  ".....  " + icounterSub + ". Wert (" + iSub+1 + ". Kindknoten, " + MyXPathParserUtilFGL.computeLineNode4ChildValue(nodeSubSub) + ")"; //Hole also den Wert.
							System.out.println( MyXPathParserUtilFGL.computeLineLeveledString(sLine, iLevel));
						}else{
							sLine = "..... " + MyXPathParserUtilFGL.computeLineNode(nodeSubSub) + ")"; //Kein Wert, darum keine ... value ... Methode aufrufen.
							System.out.println( MyXPathParserUtilFGL.computeLineLeveledString(sLine, iLevel));
						}
					}
						
//				}else{
//					icounter++;
					//System.out.println( icounter + ". Wert (" + i+1 + ". Knoten, Knotentname:Typ:Knotenwert) | " + nodeList.item(i).getNodeName() + " : " + nodeSub.getFirstChild().getNodeValue());
//					sLine = icounter + ". Wert (" + i+1 + ". Knoten, " + MyXPathParserUtilFGL.computeLineNodeValue(nodeSub) + ")";
//					System.out.println( MyXPathParserUtilFGL.computeLineLeveledString(sLine, iLevel));
				}//end if : Node.ELEMENT_NODE
			}
			return bReturn;
			
		}

}
