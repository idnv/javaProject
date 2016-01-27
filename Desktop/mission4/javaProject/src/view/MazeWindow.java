package view;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.State;
import presenter.Properties;

public class MazeWindow extends BasicWindow {

//	Timer timer;
//	TimerTask task;
	int[][] currentCrossedMaze;
	String mazeName;
	String fileToSave;
	String filePathToLoad;
	String loadFunctionNewMazeName;
	String numOfRows,numOfColumns,numOfFloors;
	MazeDisplayer mazeDisplayer;
	Position charchterPosition;
	Button generateNewMazeButton;
	AbsCharacter player;
	
	/**
	 * openPropertiesListener set what to do when the user wants set the program properties (click the open properties button)
	 */
	SelectionListener openPropertiesListener;
	/**
	 * generateMazeListener set what to do when the user requests to generate new maze in a file (click the generate maze button)
	 */
	SelectionListener generateMazeListener;
	/**
	 * clueListener set what to do when the user requests a clue (click the clue button)
	 */
	SelectionListener clueListener;
	/**
	 * solveListener set what to do when the user requests to auto solve the maze (click the solve button)
	 */
	SelectionListener solveListener;
	/**
	 * solveListener set what to do when the user requests to save the maze in a file (click the save maze button)
	 */
	SelectionListener saveMazeListener;
	/**
	 * solveListener set what to do when the user requests to load the maze from a file (click the load maze button)
	 */
	SelectionListener loadMazeListener;
	/**
	 * exitListener set what to do when the user requests to exit the program (click the solve button)
	 */
	DisposeListener exitListener;
	/**
	 * keyPressedListener set what to do when the user press a key from keyboard
	 */
	KeyListener keyListener;
	
	
	MenuItem mazeGetSoulutionItem, mazeGetHintItem, mazeSaveMazeItem, mazeLoadMazeItem;
	
	public MazeWindow(String title, int width, int height) {
		super(title, width, height);
		Image charchter;
		try {
			charchter = new Image(display, new FileInputStream("resources/wall.jpg"));
			this.player = new TuityCharachter(shell,new Position());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Menu menuBar, fileMenu, helpMenu;
	//MenuItem fileMenuHeader helpMenuHeader;
	
	@Override
	void initWidgets() {
		
		shell.setLayout(new GridLayout(2,false));
		shell.addListener(SWT.Close, new Listener() {
			
			@Override
			public void handleEvent(Event arg0) {
				System.out.println("User clicked close button");
			}
		});
		
		shell.addDisposeListener(exitListener);
		
		// ------------------- Menu Bar -------------------------- //	
		Menu menuBar, fileMenu, helpMenu, mazeMenu;
		MenuItem fileHeader ,helpHeader, mazeHeader;
		MenuItem fileExitItem,fileOpenPropertiesItem;
		MenuItem helpGameInstructions;
		
		menuBar = new Menu(shell, SWT.BAR);
		
		fileHeader = new MenuItem(menuBar, SWT.CASCADE);
		fileHeader.setText("File");
		mazeHeader = new MenuItem(menuBar, SWT.CASCADE);
		mazeHeader.setText("Maze");
		helpHeader = new MenuItem(menuBar, SWT.CASCADE);
		helpHeader.setText("Help");
		
		fileMenu = new Menu(shell,SWT.DROP_DOWN);
		fileHeader.setMenu(fileMenu);
		
		helpMenu = new Menu(shell, SWT.DROP_DOWN);
		helpHeader.setMenu(helpMenu);
		
		mazeMenu = new Menu(shell, SWT.DROP_DOWN);
		mazeHeader.setMenu(mazeMenu);
		
		// ---------------------------------------- openProperties (at menu bar) ----------------------------- //
		fileOpenPropertiesItem = new MenuItem(fileMenu,SWT.PUSH);
		fileOpenPropertiesItem.setText("Open Properties");
		fileOpenPropertiesItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				
				Properties p = new Properties();
				ClassAutoForm win= new ClassAutoForm("Set Properties", p.getClass(), shell);
				win.run();
				if(win.isSuccessfullyCreated){
					p = (Properties)win.getNewCreatedClass();
					openPropertiesListener.widgetSelected(arg0);
				}	
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		// ---------------------------------------- Exit (at menu bar) ----------------------------- //
		fileExitItem = new MenuItem(fileMenu, SWT.PUSH);
		fileExitItem.setText("Exit");
		fileExitItem.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					shell.dispose();
					
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
				}
			});
		// ---------------------------------------- GetSolution (at menu bar) ----------------------------- //
		mazeGetSoulutionItem = new MenuItem(mazeMenu, SWT.PUSH);
		mazeGetSoulutionItem.setText("Get Solution");
		mazeGetSoulutionItem.setEnabled(false);
		mazeGetSoulutionItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				solveListener.widgetSelected(arg0);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		// ---------------------------------------- GetHint (at menu bar) ----------------------------- //
		mazeGetHintItem = new MenuItem(mazeMenu, SWT.PUSH);
		mazeGetHintItem.setText("Get Hint");
		mazeGetHintItem.setEnabled(false);
		// ---------------------------------------- GameInstructions(at menu bar) ----------------------------- //
		helpGameInstructions = new MenuItem(helpMenu,SWT.PUSH);
		helpGameInstructions.setText("Game Instructions");					
		// ---------------------------------------- SaveMaze(at menu bar) ----------------------------- //
		mazeSaveMazeItem = new MenuItem(mazeMenu,SWT.PUSH);
		mazeSaveMazeItem.setText("Save Maze");
		mazeSaveMazeItem.setEnabled(false);
		mazeSaveMazeItem.addSelectionListener(saveMazeListener);
		// ---------------------------------------- LoadMaze(at menu bar) ----------------------------- //
		mazeLoadMazeItem = new MenuItem(mazeMenu,SWT.PUSH);
		mazeLoadMazeItem.setText("Load Maze");				
		mazeLoadMazeItem.addSelectionListener(loadMazeListener);
		shell.setMenuBar(menuBar);
		// ---------------------- generateNewMazeButton -------------------------------//
		generateNewMazeButton=new Button(shell, SWT.PUSH);
		generateNewMazeButton.setText("Generate New Maze");
		generateNewMazeButton.setLayoutData(new GridData(SWT.FILL, SWT.None, false, false, 1, 1));
		generateNewMazeButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				generateNewMazeButton.setEnabled(false);
				mazeSaveMazeItem.setEnabled(false);
				mazeLoadMazeItem.setEnabled(false);
				MazeProperties mazePropertiesWin= new MazeProperties("Set Maze Properties",500,500, shell);
				mazePropertiesWin.run();
				if(mazePropertiesWin.isChangeSucceeded()){
					setMazeName(mazePropertiesWin.getMazeName()); 
					setNumOfFloors(mazePropertiesWin.getNumOfFloors());
					setNumOfRows(mazePropertiesWin.getNumOfRows());
					setNumOfColumns(mazePropertiesWin.getNumOfColumns());
					generateMazeListener.widgetSelected(arg0);
				}
				else{
					generateNewMazeButton.setEnabled(true);
					mazeSaveMazeItem.setEnabled(true);
					mazeLoadMazeItem.setEnabled(true);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				
				
			}
		});	

		// -----------------MazeDisplayer------------------------- //
		mazeDisplayer = new Maze2D(shell, SWT.DOUBLE_BUFFERED, player);		
		//mazeDisplayer=new Maze3dDisplayByFloor(shell, SWT.BORDER | SWT.DOUBLE_BUFFERED);
		mazeDisplayer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,true,1,1));
	
		// ----------------------------- Maze displayer key relesed ---------------------- //
		mazeDisplayer.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				if(!mazeDisplayer.isWon()){
					keyListener.keyReleased(arg0);
				}
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {				
				if(!mazeDisplayer.isWon()){
					if(arg0.keyCode == SWT.ARROW_LEFT){
						mazeDisplayer.moveLeft();
						charchterPosition.setRow(mazeDisplayer.getRow());
						charchterPosition.setColumn(mazeDisplayer.getColumn());
						System.out.println("Charachter move LEFT to" + charchterPosition);
					}
					if(arg0.keyCode == SWT.ARROW_RIGHT){
						mazeDisplayer.moveRight();
						charchterPosition.setRow(mazeDisplayer.getRow());
						charchterPosition.setColumn(mazeDisplayer.getColumn());
						System.out.println("Charachter move RIGHT to" + charchterPosition);
					}
					if(arg0.keyCode == SWT.ARROW_DOWN){
						mazeDisplayer.moveDown();
						charchterPosition.setRow(mazeDisplayer.getRow());
						charchterPosition.setColumn(mazeDisplayer.getColumn());
						System.out.println("Charachter move FORWARD to" + charchterPosition);
					}
						
					if(arg0.keyCode == SWT.ARROW_UP){
						mazeDisplayer.moveUp();
						charchterPosition.setRow(mazeDisplayer.getRow());
						charchterPosition.setColumn(mazeDisplayer.getColumn());	
						System.out.println("Charachter move BACKWARDS to" + charchterPosition);
					}	
				}
			}
			
			});
	}
		
	public void setGoalPosition(int row, int col){
		mazeDisplayer.setGoalPosition(row, col);
	}
	
	
	public void displayEror(String eror){
		Display.getCurrent().syncExec(new Runnable() {
				
				@Override
				public void run() {
					MessageBox errorBox =  new MessageBox(shell, SWT.ICON_ERROR); 
					errorBox.setMessage(eror);
					errorBox.setText("Error");
					errorBox.open();				
				}
			});
	}
	
	public void messageToUser(String eror){
		Display.getCurrent().syncExec(new Runnable() {
				
				@Override
				public void run() {
					MessageBox errorBox =  new MessageBox(shell, SWT.ICON_INFORMATION); 
					errorBox.setMessage(eror);
					errorBox.setText("Information");
					errorBox.open();				
				}
			});
	}

	public void newGeneratedSolution(ArrayList<State<Position>> arr){
		
	}
	
	public void displayCrossSectionOfMaze(int[][] crossedMaze){
		this.currentCrossedMaze = crossedMaze;
		mazeDisplayer.setMazeData(crossedMaze);
		mazeDisplayer.setCharacterPosition(charchterPosition.getRow(), charchterPosition.getColumn());
		mazeDisplayer.setWon(false);
		System.out.println("SET CHARACHTER TO: " + charchterPosition);
		display.syncExec(new Runnable() {
			
			@Override
			public void run() {
				generateNewMazeButton.setEnabled(true);
				mazeGetSoulutionItem.setEnabled(true);
				mazeGetHintItem.setEnabled(true);
				mazeLoadMazeItem.setEnabled(true);
				mazeSaveMazeItem.setEnabled(true);
			}			
		});
	}

	public void moveCharchter(Position p){
		this.charchterPosition = p;
		mazeDisplayer.setCharacterPosition(p.getRow(), p.getColumn());
	}
	
	
	/**
	 * @param keyPressedListener the keyPressedListener to set
	 */
	public void setKeyPressedListener(KeyListener keyPressedListener) {
		this.keyListener = keyPressedListener;
	}

	/**
	 * @param generateMazeListener the generateMazeListener to set
	 */
	public void setGenerateMazeListener(SelectionListener generateMazeListener) {
		this.generateMazeListener = generateMazeListener;
	}


	/**
	 * @param clueListener the clueListener to set
	 */
	public void setClueListener(SelectionListener clueListener) {
		this.clueListener = clueListener;
	}

	/**
	 * @param solveListener the solveListener to set
	 */
	public void setSolveListener(SelectionListener solveListener) {
		this.solveListener = solveListener;
	}
	
	/**
	 * @param saveMazeListener the saveMazeListener to set
	 */
	public void setSaveMazeListener(SelectionListener saveMazeListener) {
		this.saveMazeListener = saveMazeListener;
	}

	/**
	 * @param loadMazeListener the loadMazeListener to set
	 */
	public void setLoadMazeListener(SelectionListener loadMazeListener) {
		this.loadMazeListener = loadMazeListener;
	}

	/**
	 * @param exitListener the exitListener to set
	 */
	public void setExitListener(DisposeListener exitListener) {
		this.exitListener = exitListener;
	}

	/**
	 * @return the mazeName
	 */
	public String getMazeName() {
		return mazeName;
	}

	/**
	 * @param mazeName the mazeName to set
	 */
	public void setMazeName(String mazeName) {
		this.mazeName = mazeName;
	}

	/**
	 * @return the fileToSave
	 */
	public String getFileToSave() {
		return fileToSave;
	}

	/**
	 * @param fileToSave the fileToSave to set
	 */
	public void setFileToSave(String fileToSave) {
		this.fileToSave = fileToSave;
	}

	/**
	 * @return the filePathToLoad
	 */
	public String getFilePathToLoad() {
		return filePathToLoad;
	}

	/**
	 * @param filePathToLoad the filePathToLoad to set
	 */
	public void setFilePathToLoad(String filePathToLoad) {
		this.filePathToLoad = filePathToLoad;
	}

	/**
	 * @return the numOfRows
	 */
	public String getNumOfRows() {
		return numOfRows;
	}

	/**
	 * @return the numOfColumns
	 */
	public String getNumOfColumns() {
		return numOfColumns;
	}

	/**
	 * @return the numOfFloors
	 */
	public String getNumOfFloors() {
		return numOfFloors;
	}

	/**
	 * @return the loadFunctionNewMazeName
	 */
	public String getLoadFunctionNewMazeName() {
		return loadFunctionNewMazeName;
	}

	/**
	 * @param loadFunctionNewMazeName the loadFunctionNewMazeName to set
	 */
	public void setLoadFunctionNewMazeName(String loadFunctionNewMazeName) {
		this.loadFunctionNewMazeName = loadFunctionNewMazeName;
	}
	
	/**
	 * @param numOfRows the numOfRows to set
	 */
	public void setNumOfRows(String numOfRows) {
		this.numOfRows = numOfRows;
	}

	/**
	 * @param numOfColumns the numOfColumns to set
	 */
	public void setNumOfColumns(String numOfColumns) {
		this.numOfColumns = numOfColumns;
	}

	/**
	 * @param numOfFloors the numOfFloors to set
	 */
	public void setNumOfFloors(String numOfFloors) {
		this.numOfFloors = numOfFloors;
	}

	/**
	 * @return the charchterPosition
	 */
	public Position getCharchterPosition() {
		return charchterPosition;
	}

	/**
	 * @param charchterPosition the charchterPosition to set
	 */
	public void setCharchterPosition(Position charchterPosition) {
		this.charchterPosition = charchterPosition;
	}
	

	/**
	 * @param openProperties the openProperties to set
	 */
	public void setOpenProperties(SelectionListener openProperties) {
		this.openPropertiesListener = openProperties;
	}

	public Shell getShell() {return shell;}
	
}
