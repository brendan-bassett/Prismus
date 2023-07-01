package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;

import project.Ratio;

public abstract interface STYLE {

//	TODO: find a way to select fonts out of what is available using GraphicsEnviroment.getAvailableFontFamilyNames()

	interface GLOBAL {

		Color	BORDER_COLOR			= Color.BLACK;
		Color	TEXT_COLOR				= Color.BLACK;

		Color	FOCUSED_BORDER_COLOR	= Color.RED;
		Color	FOCUSED_TEXT_COLOR		= Color.RED;


		interface CURSOR {

			Cursor	DEFAULT	= new Cursor(Cursor.DEFAULT_CURSOR);
			Cursor	HAND	= new Cursor(Cursor.HAND_CURSOR);
			Cursor	MOVE	= new Cursor(Cursor.MOVE_CURSOR);

			Cursor	VERT	= new Cursor(Cursor.N_RESIZE_CURSOR);
			Cursor	HOZ		= new Cursor(Cursor.W_RESIZE_CURSOR);
			Cursor	NE		= new Cursor(Cursor.NE_RESIZE_CURSOR);
			Cursor	SE		= new Cursor(Cursor.SE_RESIZE_CURSOR);

		}

		interface STROKE {

			BasicStroke	BASIC_1	= new BasicStroke(1);
			BasicStroke	BASIC_2	= new BasicStroke(2);
			BasicStroke	BASIC_3	= new BasicStroke(3);
			BasicStroke	BASIC_4	= new BasicStroke(4);

		}

	}


	interface CREATE_NOTE extends GLOBAL {

		Color	INVALID_COLOR			= Color.RED;
		Color	SELECTED_COLOR			= Color.GREEN;
		Color	UNSELECTED_COLOR		= Color.BLUE.darker();
		Color	VALID_COLOR				= Color.BLACK;

		int		SELECTED_LINE_LENGTH	= 100;
		int		TEXT_MARGIN				= 3;
		int		UNSELECTED_LINE_LENGTH	= 80;
		int		UNISON_RADIUS			= 5;

	}

	interface GRAPH extends GLOBAL {

		int			BEAT_NUMBER_RECT_SIZE	= 7;
		int			MEASURE_NUM_Y_MARGIN	= 4;

		int			START_LINES_D_X			= 6;

		float		BEAT_SUBDIVISION		= 0.5f;

		Color		BEAT_COLOR				= Color.LIGHT_GRAY;
		Color		MEASURE_NUM_BG_COLOR	= Color.WHITE;

		Color		PRIME_TONIC_COLOR		= Color.BLACK;
		Color		START_LINE_COLOR		= Color.BLACK;
		Color		TONIC_COLOR				= Color.DARK_GRAY;
		Color		ZERO_BEAT_COLOR			= Color.DARK_GRAY;

		CustomFont	MEASURE_FONT			= new CustomFont("Times New Roman", Font.PLAIN, 10);

		BasicStroke	PRIME_TONIC_STROKE		= STROKE.BASIC_2;
		BasicStroke	START_LINE_STROKE		= STROKE.BASIC_2;
		BasicStroke	ZERO_BEAT_STROKE		= STROKE.BASIC_2;

		Insets		MEASURE_NUM_INSETS		= new Insets(2, 2, 2, 2);

	}

	interface INTERVAL_WINDOW extends GLOBAL {

		int			DEFAULT_HEIGHT	= 700;
		int			DEFAULT_WIDTH	= 1000;

		Color		ROOT_TEXT_COLOR	= Color.BLACK;
		CustomFont	ROOT_FONT		= new CustomFont("Times New Roman", Font.BOLD, 10);
	}

	interface MULTI_SELECT extends GLOBAL {

		Color	BORDER_COLOR	= Color.RED;
		Color	OVERLAY_COLOR	= new Color(255, 0, 0, 0.3f);	//pure red @ %30 transparency
		Color	TEMP_NOTE_COLOR	= Color.GRAY;

	}

	interface NOTE extends GLOBAL {

		int			CONTAINS_MARGIN				= 3;

		int			COMPLETE_TRIANGLE_SIZE		= 5;
		float		COMPLETE_REL_P_HEIGHT		= .05f;

		int			INCOMPLETE_SIZE				= 5;
		int			INCOMPLETE_WIDTH			= 100;

		int			BEND_LIMIT					= 50;
		float		BEND_PER_PIXEL				= 0.35f;


		int			INCOMPLETE_MARGIN_FROM_NOTE	= 6;
		int			MARGIN_FROM_NOTE			= 3;
		int			INTERVAL_TEXT_MARGIN		= 2;


		Color		COMPLETE_VALID_COLOR		= Color.GREEN.darker();
		Color		COMPLETE_INVALID_COLOR		= Color.RED;
		Color		COMPLETE_FILL_COLOR			= Color.LIGHT_GRAY;

		Color		INCOMPLETE_BORDER_COLOR		= Color.BLACK;

		Color		MULTI_SELECT_COLOR			= Color.BLUE;

		Color		NEW_VALID_COLOR				= Color.GREEN.darker();
		Color		NEW_INVALID_COLOR			= Color.RED;

		Color		BEND_LINE_COLOR				= Color.BLACK;
		Color		MARGIN_COLOR				= Color.GREEN;

		Color		RATIO_BACKGROUND_COLOR		= Color.WHITE;
		Color		RATIO_CONTAINS_RECT_COLOR	= Color.GREEN;

		Color		RATIO_TEXT_COLOR			= Color.BLACK;

		CustomFont	FONT						= new CustomFont("Interval", Font.PLAIN, 10);
	}

	interface PLAY_MARKER extends GLOBAL {

		int			TOP_TRIANGLE_SIZE	= 10;

		int			WINDOW_MOVE_RIGHT	= 100;
		int			WINDOW_MOVE_LEFT	= 200;


		Color		LINE_COLOR			= Color.BLUE;
		Color		TOP_TRIANGLE_FILL	= Color.LIGHT_GRAY;

		BasicStroke	LINE_STROKE			= STROKE.BASIC_1;
		BasicStroke	TOP_TRIANGLE_STROKE	= STROKE.BASIC_1;

	}

	interface PROJECT extends GLOBAL {

		float	HZ_BOTTOM_LIMIT			= 35;				// for a 32' open pipe
		float	HZ_TOP_LIMIT			= 6800;				// for a 2" open pipe
		float	DEFAULT_HARMONIC_CENTER	= 440;

		Ratio	R11						= new Ratio(1, 1);
		Ratio	R98						= new Ratio(9, 8);
		Ratio	R76						= new Ratio(7, 6);
		Ratio	R65						= new Ratio(6, 5);
		Ratio	R54						= new Ratio(5, 4);
		Ratio	R43						= new Ratio(4, 3);
		Ratio	R75						= new Ratio(7, 5);
		Ratio	R32						= new Ratio(3, 2);
		Ratio	R85						= new Ratio(8, 5);
		Ratio	R53						= new Ratio(5, 3);
		Ratio	R74						= new Ratio(7, 4);
		Ratio	R95						= new Ratio(9, 5);

	}

	interface OVERTONES extends GLOBAL {

		int		OVERTONES_SHOWN	= 16;

		Color	COLOR			= Color.LIGHT_GRAY;

	}

	interface SOUND_MENU extends GLOBAL {


	}

	interface TIME_MARKER extends GLOBAL {

		int		TOP_TRIANGLE_SIZE		= 10;
		int		BOTTOM_TRIANGLE_SIZE	= 5;


		Color	FILL_COLOR				= Color.LIGHT_GRAY;

		Color	BORDER_COLOR			= new Color(51, 0, 102);

	}

	interface TONIC_BUTTON extends GLOBAL {

		int			RADIUS				= 12;
		int			X_MARGIN			= 5;												// minimum distance from left of screen to left of button
		// ALSO distance from right of button to left of double start lines
		int			TEXT_CENTER_BALANCE	= 1;

		String		TONIC_TEXT			= "T";

		Color		FILL_COLOR			= Color.LIGHT_GRAY;

		Color		FONT_COLOR			= Color.BLACK;

		CustomFont	PRIME_FONT			= new CustomFont("Times New Roman", Font.BOLD, 10);
		CustomFont	FONT				= new CustomFont("Times New Roman", Font.PLAIN, 10);

	}

	interface SCORE_WINDOW extends GLOBAL {

		int		DEFAULT_WIDTH			= 1080;
		int		DEFAULT_HEIGHT			= 720;

		int		DRAW_MARGIN				= 25;		// number of pixels beyond the window that will be drawn

		int		MOVE_PER_SCROLL			= 50;
		float	H_ZOOM_IN_PER_SCROLL	= 1.05f;
		float	H_ZOOM_OUT_PER_SCROLL	= 0.95f;
		float	V_ZOOM_IN_PER_SCROLL	= 1.05f;
		float	V_ZOOM_OUT_PER_SCROLL	= 0.95f;

		int		SCORE_START_MARGIN		= 60;		// ...in Pixels
		float	INIT_REL_P				= -0.5f;	// ...in Octaves

		int		PPS_INIT				= 100;
		int		PPS_MIN					= 20;
		int		PPS_MAX					= 2000;

		int		PPO_INIT				= 300;
		int		PPO_MIN					= 20;
		int		PPO_MAX					= 2000;

	}

}
