import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class MicroMouseApp extends JFrame implements ActionListener {
	Container cPane;
	JPanel panelView2D, panelView3D, panelCreate,
		panelV3DUP, panelControl;
	Maze2DCanvas maze2DCanvas;
	Maze3DCanvas maze3DCanvas;
	JButton buttonCreate, buttonStart;
	
	Nezumi nezumi;
	Map  map;
	
	NezumiThread ns;

	public static void main(String[] args) {
		new MicroMouseApp();
	}

	public MicroMouseApp() {
		super( "迷路を歩き回る（だけ）" );
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		guiSetting();

		nezumi = new Nezumi(1, 1);
		maze2DCanvas.setNezumi( nezumi );
		maze3DCanvas.setNezumi( nezumi );
		
		map = new Map(31, 31);
		maze2DCanvas.setMap( map );
		maze3DCanvas.setMap( map );
	}
		
		
	public void guiSetting() {	
		cPane = this.getContentPane();
		cPane.setLayout( new BorderLayout() );
		
		panelView2D = new JPanel();
		maze2DCanvas = new Maze2DCanvas();
		panelCreate = new JPanel();
		buttonCreate = new JButton("Maze Create");
		buttonCreate.addActionListener( this );
		buttonStart = new JButton("MicroMouse Start");
		buttonStart.addActionListener( this );
		panelView2D.setLayout( new BorderLayout() );
		FlowLayout flowL = new FlowLayout();
		flowL.setAlignment( FlowLayout.LEFT );
		panelCreate.setLayout( flowL );
		panelCreate.add( buttonCreate );
		panelCreate.add( buttonStart );
		panelView2D.add( maze2DCanvas, BorderLayout.CENTER );
		panelView2D.add( panelCreate, BorderLayout.SOUTH );
		
		panelView3D = new JPanel();
		panelView3D.setBackground( new Color(128, 100, 100) );
		panelView3D.setLayout( new GridLayout(2, 1));
		panelV3DUP = new JPanel();
		panelV3DUP.setSize(260, 260);
		panelV3DUP.setBackground(new Color(128,128,128));
		maze3DCanvas = new Maze3DCanvas();
		maze3DCanvas.setSize(200, 200);
		panelV3DUP.add(maze3DCanvas);
		panelView3D.add( panelV3DUP );
		panelControl = new JPanel();
		panelControl.setBackground(new Color(128, 230, 128));
		panelView3D.add(panelControl);
		
		
		
		cPane.add( panelView2D, BorderLayout.CENTER );
		cPane.add( panelView3D, BorderLayout.EAST );
		
		this.setSize(800, 500);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent evt) {
		if ( evt.getSource() == buttonCreate ) {
			
			for (int y = 0; y < map.yMax; ++y ) {
				for (int x = 0; x < map.xMax; ++x ) {
					map.masu[x][y] = Map.MICHI;
				}
			}
			

			// Step 1
			for (int x = 0; x < map.xMax; ++x ) {
				map.masu[x][0]            = Map.KABE;
				map.masu[x][map.yMax - 1] = Map.KABE;
			}
			for (int y = 0; y < map.yMax; ++y ) {
				map.masu[0][y]            = Map.KABE;
				map.masu[map.xMax - 1][y] = Map.KABE;
			}

			maze2DCanvas.repaint();
			
			// Step 2
			for (int y = 2; y < map.yMax - 2; y += 2 ) {
				for (int x = 2; x < map.xMax - 2; x += 2 ) {
					map.masu[x][y]        = Map.KABE;
				}
			}
			maze2DCanvas.repaint();

			int dx, dy;
			for (int y = 2; y < map.yMax - 2; y += 2 ) {
				dx = 0; dy = 0;
				while (map.masu[2 + dx][y + dy] == Map.KABE) {
					switch ( (int)( Math.random() * 4 ) ) {
					case 0:  // ??
						dx = 0;  dy = -1;
						break;
					case 1:  // ??
						dx = -1;  dy = 0;
						break;
					case 2: // ??
						dx = 0;  dy = 1;
						break;
					default: // ?E
						dx = 1;  dy = 0;
						break;
					}
				}
				map.masu[2 + dx][y + dy] = Map.KABE;
			}
			
			maze2DCanvas.repaint();

			//int dx, dy;
			for (int x = 4; x < map.xMax - 2; x += 2 ) {
				for (int y = 2; y < map.yMax - 2; y += 2 ) {
					dx = 0; dy = 0;
					while (map.masu[x + dx][y + dy] == 1) {
						switch ( (int)( Math.random() * 3 ) ) {
						case 0:  //
							dx = 0;  dy = -1;
							break;
						case 1:  //
							dx = 0;  dy = 1;
							break;
						default: //
							dx = 1;  dy = 0;
							break;
						}
					}
					map.masu[x + dx][y + dy] = Map.KABE;
				}
			}
			
			maze2DCanvas.repaint();			
			maze3DCanvas.repaint();
		}
		else if ( evt.getSource() == buttonStart ) {
			ns = new NezumiThread( this );
			ns.start();
		}
	}
}

class NezumiThread extends Thread {
	MicroMouseApp oya;
	
	int mawaruHoukouFlag = 1;
	int chokushinFlag = 0;
	
	public NezumiThread(MicroMouseApp oya) {
		this.oya = oya;
	}
	public void run() {
	
		while ( true ) {
			try {
				if ( chokushinFlag == 1 && oya.nezumi.senseFront( oya.map ) == Map.MICHI ) {
					oya.nezumi.goFoward();
					chokushinFlag = 0;
				}
				else if ( oya.nezumi.senseLeft( oya.map ) == Map.MICHI ) {
					oya.nezumi.turnLeft();
					chokushinFlag = 1;
				}
				else if ( oya.nezumi.senseFront( oya.map ) == Map.MICHI ) {
					oya.nezumi.goFoward();
				}
				else if ( oya.nezumi.senseRight( oya.map ) == Map.MICHI ) {
					oya.nezumi.turnRight();
					chokushinFlag = 1;
				}
				else {
					//if ( mawaruHoukouFlag == 1 ) {
						oya.nezumi.turnLeft();
					//	mawaruHoukouFlag = -1;
					//}
					//else {
					//	oya.nezumi.turnRight();
					//	mawaruHoukouFlag =  1;
					//}
				}

				oya.maze2DCanvas.repaint();
				oya.maze3DCanvas.repaint();
				
				Thread.sleep( 200 );
				
			} catch ( InterruptedException e ) {
				e.printStackTrace();
				System.exit( 1 );
			}
		}
		
	}
}



class Maze2DCanvas extends Canvas {
	Nezumi nezumi;
	Map map;

	public void setNezumi(Nezumi nezumi) {
		this.nezumi = nezumi;
	}

	public void setMap(Map map) {
		this.map = map;
	}
	
	public void paint(Graphics g) {
		if ( nezumi != null && map != null ) {
			for (int y = 0; y < map.yMax; ++y) {
				for (int x = 0; x < map.xMax; ++x) {
					//System.out.print( map.masu[x][y] );
					switch (map.masu[x][y]) {
					case 0:
						g.drawRect(100 + x*12, 20 + y*12, 12, 12);
						//System.out.print(" ");
						break;
					case 1:
						g.fillRect(100 + x*12, 20 + y*12, 12, 12);
						//System.out.print("X");
						break;
					}
				}
				System.out.println();
			}
			
			g.setColor( Color.RED );
			g.fillRect(100 + nezumi.getX() *12, 20 + nezumi.getY() *12, 12, 12);
			g.setColor( Color.BLACK );
			
		}
		else {
			g.drawString("地図がまだ用意されていません", 10, 100);
		}
	}

}

class Maze3DCanvas extends Canvas {
	Nezumi nezumi;
	Map map;
	
	public Maze3DCanvas() {
		super();
		this.setBackground(new Color(210, 210, 210));
	}
	
	public void setNezumi(Nezumi nezumi) {
		this.nezumi = nezumi;
	}
	
	public void setMap(Map map) {
		this.map = map;
	}
	
	public void paint(Graphics g) {
		if ( nezumi != null && map != null ) {
			/*
			g.drawLine(0,0,90,90);
			g.drawLine(0,200,90,110);
			g.drawLine(110,90,200,0);
			g.drawLine(110,110,200,200);
			g.drawRect(90,90,20,20);
			g.drawLine(70,70,70,130);
			g.drawLine(130,70,130,130);
			g.drawLine(40,40,40,160);
			g.drawLine(160,40,160,160);
			*/
			
			if ( hito.checkPoint( map, 1 ) == Map.KABE ) {
				System.out.println("左は壁");
				g.drawLine(0,0,25,25);
				g.drawLine(25,25,25,175);
				g.drawLine(25,175,0,200);
			}
			else {
				System.out.println("左は道");
				g.drawLine(0,25,25,25);
				g.drawLine(25,25,25,175);
				g.drawLine(25,175,0,175);
			}
			if ( hito.checkPoint( map, 2 ) == Map.KABE ) {
				System.out.println("右は壁");
				g.drawLine(200,0,175,25);
				g.drawLine(175,25,175,175);
				g.drawLine(175,175,200,200);
			}
			else {
				System.out.println("右は道");
				g.drawLine(200,25,175,25);
				g.drawLine(175,25,175,175);
				g.drawLine(175,175,200,175);
			}
			if ( hito.checkPoint( map, 0 ) == Map.KABE ) {
				System.out.println("一歩前は壁");
				g.drawRect(25,25,150,150);
			}
			else {
				if ( hito.checkPoint( map, 4 ) == Map.KABE ) {
					System.out.println("一歩先の左は壁");
					g.drawLine(25,25,65,65);
					g.drawLine(65,65,65,135);
					g.drawLine(65,135,25,175);
				}
				else {
					System.out.println("一歩先の左は道");
					g.drawLine(25,65,65,65);
					g.drawLine(65,65,65,135);
					g.drawLine(65,135,25,135);
				}
				if ( hito.checkPoint( map, 5 ) == Map.KABE ) {
					System.out.println("一歩先の右は壁");
					g.drawLine(175,25,135,65);
					g.drawLine(135,65,135,135);
					g.drawLine(135,135,175,175);
				}
				else {
					System.out.println("一歩先の右は道");
					g.drawLine(175,65,135,65);
					g.drawLine(135,65,135,135);
					g.drawLine(135,135,175,135);
				}
				if ( hito.checkPoint( map, 3 ) == Map.KABE ) {
					System.out.println("２歩前は壁");
					g.drawRect(65,65,70,70);
				}
				else {
					if ( hito.checkPoint( map, 7 ) == Map.KABE ) {
						System.out.println("２歩先の左は壁");
						g.drawLine(65,65,85,85);
						g.drawLine(85,85,85,115);
						g.drawLine(85,115,65,135);
					}
					else {
						System.out.println("２歩先の左は道");
						g.drawLine(65,85,85,85);
						g.drawLine(85,85,85,115);
						g.drawLine(85,115,65,115);
					}
					if ( hito.checkPoint( map, 8 ) == Map.KABE ) {
						System.out.println("２歩先の右は壁");
						g.drawLine(135,65,115,85);
						g.drawLine(115,85,115,115);
						g.drawLine(115,115,135,135);
					}
					else {
						System.out.println("２歩先の右は道");
						g.drawLine(135,85,115,85);
						g.drawLine(115,85,115,115);
						g.drawLine(115,115,135,115);
					}
					if ( hito.checkPoint( map, 6 ) == Map.KABE ) {
						System.out.println("３歩前は壁");
						g.drawRect(85,85,30,30);
					}
					else {
						if ( hito.checkPoint( map, 10 ) == Map.KABE ) {
							System.out.println("３歩先の左は壁");
							g.drawLine(85,85,95,95);
							g.drawLine(95,95,95,105);
							g.drawLine(95,105,85,115);
						}
						else {
							System.out.println("３歩先の左は道");
							g.drawLine(85,95,95,95);
							g.drawLine(95,95,95,105);
							g.drawLine(95,105,85,105);
						}
						if ( hito.checkPoint( map, 11 ) == Map.KABE ) {
							System.out.println("３歩先の右は壁");
							g.drawLine(115,85,105,95);
							g.drawLine(105,95,105,105);
							g.drawLine(105,105,115,115);
						}
						else {
							System.out.println("３歩先の右は道");
							g.drawLine(115,95,105,95);
							g.drawLine(105,95,105,105);
							g.drawLine(105,105,115,105);
						}
						if ( hito.checkPoint( map, 9 ) == Map.KABE ) {
							System.out.println("４歩前は壁");
							g.drawRect(95,95,10,10);
						}
						else {
							System.out.println("４歩前は道");
							g.drawLine(95,95,105,105);
							g.drawLine(105,95,95,105);
						}
						
					}
					
				}
				
			}
			
		}
	}
}

class Nezumi {
	private int x, y;
	private int muki = Muki.SOUTH;

	public Nezumi(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getMuki() {
		return muki;
	}

	public int senseFront(Map map) {
		return checkPoint(map, 0);
	}
	public int senseLeft(Map map) {
		return checkPoint(map, 1);
	}
	public int senseRight(Map map) {
		return checkPoint(map, 2);
	}
	
	public int checkPoint(Map map, int point) {
		if (map == null) {
			return -1;
		}
		else {
			return map.checkPoint(x, y, muki, point);
		}
	}
	
	
	public void goFoward() {
		switch (muki) {
		case Muki.NORTH:
			--y;
			break;
		case Muki.EAST:
			++x;
			break;
		case Muki.SOUTH:
			++y;
			break;
		case Muki.WEST:
			--x;
			break;
		}
	}
	public void turnRight() {
		switch (muki) {
		case Muki.NORTH:
			muki = Muki.EAST;
			break;
		case Muki.EAST:
			muki = Muki.SOUTH;
			break;
		case Muki.SOUTH:
			muki = Muki.WEST;
			break;
		case Muki.WEST:
			muki = Muki.NORTH;
			break;
		}
	}
	public void turnLeft() {
		switch (muki) {
		case Muki.NORTH:
			muki = Muki.WEST;
			break;
		case Muki.EAST:
			muki = Muki.NORTH;
			break;
		case Muki.SOUTH:
			muki = Muki.EAST;
			break;
		case Muki.WEST:
			muki = Muki.SOUTH;
			break;
		}
	}
}

abstract class Muki {
	static final int NORTH = 0;
	static final int EAST  = 1;
	static final int SOUTH = 2;
	static final int WEST  = 3;
}

class Map {
	static final int MICHI = 0;
	static final int KABE  = 1;
	
	int xMax, yMax;
	int[][] masu;

	int[][] ddx = {
			{  0, -1,  1,  0, -1,  1,  0, -1,  1,  0, -1,  1 } ,  // NORTH
			{  1,  0,  0,  2,  1,  1,  3,  2,  2,  4,  3,  3 } ,  // EAST
			{  0,  1, -1,  0,  1, -1,  0,  1, -1,  0,  1, -1 } ,  // SOUTH
			{ -1,  0,  0, -2, -1, -1, -3, -2, -2, -4, -3, -3 } }; // WEST
	int[][] ddy = {
			{ -1,  0,  0, -2, -1, -1, -3, -2, -2, -4, -3, -3 } ,  // NORTH
			{  0, -1,  1,  0, -1,  1,  0, -1,  1,  0, -1,  1 } ,  // EAST
			{  1,  0,  0,  2,  1,  1,  3,  2,  2,  4,  3,  3 } ,  // SOUTH
			{  0,  1, -1,  0,  1, -1,  0,  1, -1,  0,  1, -1 } }; // WEST
	
	public Map(int xMax, int yMax) {
		// 盤の大きさを設定
		this.xMax = xMax;	this.yMax = yMax;
		masu = new int[xMax][yMax];
		
		// 枡を初期化（すべて道にする）
		for( int y = 0; y < yMax; ++y ) {
			for( int x = 0; x < xMax; ++x ) {
				masu[x][y] = Map.MICHI;
			}
		}
	}
	
	public int checkPoint(int x, int y, int muki, int point) {
		return masu[ x + ddx[muki][point] ][ y + ddy[muki][point] ];
	}
	
	

	public void setMasu(int x, int y, int n) {
		if (x >= 0 && x < xMax & y >= 0 && y < yMax) {
			masu[x][y] = n;
		}
		else {
			System.out.println("範囲外のマスです。");
		}
	}
	
	public int getMasu(int x, int y) {
		return masu[x][y];
	}
}
