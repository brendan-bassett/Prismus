package file;

abstract public interface FILE {


	interface GRAPH_F {

		Tag			TAG					= new Tag("GRAPH");
		Tag			RATIO_TAG			= new Tag("RATIO");


		IntAttr		BEATS_PER_MEASURE	= new IntAttr("beatsPerMeasure");
		FloatAttr	END_TIME			= new FloatAttr("endTime");
		IntAttr		ID					= new IntAttr("id");
		IntAttr		LEAD_BEATS			= new IntAttr("leadBeats");
		IntAttr		MEASURES			= new IntAttr("measures");
		IntAttr		MEASURE_NUM_START	= new IntAttr("measureNumStart");
		FloatAttr	START_TIME			= new FloatAttr("startTime");
		FloatAttr	TEMPO				= new FloatAttr("tempo");

	}

	interface NOTE_F {

		Tag			TAG					= new Tag("NOTE");
		Tag			PARENT_RATIO_TAG	= new Tag("PARENT_RATIO");
		Tag			RATIO_TAG			= new Tag("RATIO");

		StringAttr	ALIGN				= new StringAttr("align");
		IntAttr		BEND				= new IntAttr("bend");
		IntAttr		GRAPH_ID			= new IntAttr("graphID");
		FloatAttr	END_T				= new FloatAttr("eT");
		FloatAttr	HZ					= new FloatAttr("hz");
		IntAttr		ID					= new IntAttr("id");
		FloatAttr	IDEAL_REL_P			= new FloatAttr("idealRelP");
		IntAttr		INTERVAL_ID			= new IntAttr("intervalID");
		BooleanAttr	IS_COMPLETE			= new BooleanAttr("isComplete");
		BooleanAttr	IS_SIMPLIFIED		= new BooleanAttr("isSimplified");
		StringAttr	PART_NAME			= new StringAttr("part");
		FloatAttr	REL_P				= new FloatAttr("relP");
		BooleanAttr	SHOW_OVERTONES		= new BooleanAttr("showOvertones");
		FloatAttr	START_T				= new FloatAttr("sT");

	}

	interface PART_F {

		Tag			TAG			= new Tag("PART");

		StringAttr	NAME		= new StringAttr("name");
		IntAttr		COLOR_RGB	= new IntAttr("color");
		IntAttr		ID			= new IntAttr("id");
		BooleanAttr	IS_SELECTED	= new BooleanAttr("isSelected");

	}

	interface PROJECT_F {

		Tag			TAG							= new Tag("PROJECT");

		Tag			DEFAULT_RATIOS_TAG			= new Tag("DEFAULT_RATIOS");
		Tag			GRAPH_LIST_TAG				= new Tag("GRAPH_LIST");
		Tag			INTERVAL_FROM_NOTE_LIST_TAG	= new Tag("INTERVAL_FROM_NOTE_LIST");
		Tag			INTERVAL_LIST_TAG			= new Tag("INTERVAL_LIST");
		Tag			NOTE_LIST_TAG				= new Tag("NOTE_LIST");
		Tag			OVERTONES_LIST_TAG			= new Tag("OVERTONES_LIST");
		Tag			PART_LIST_TAG				= new Tag("PART_LIST");
		Tag			TIME_MARKER_LIST_TAG		= new Tag("TIME_MARKER_LIST");
		Tag			TONIC_BUTTON_LIST_TAG		= new Tag("TONIC_BUTTON_LIST");
		Tag			WINDOW_LIST_TAG				= new Tag("WINDOW_LIST");


		StringAttr	AUTHOR						= new StringAttr("author");
		IntAttr		GRAPH_ID_COUNTER			= new IntAttr("graphIDCounter");
		FloatAttr	HARMONIC_CENTER				= new FloatAttr("harmonicCenter");
		IntAttr		INTERVAL_ID_COUNTER			= new IntAttr("intervalIDCounter");
		StringAttr	NAME						= new StringAttr("name");
		IntAttr		NOTE_ID_COUNTER				= new IntAttr("noteIDCounter");

		String		AUTHOR_VALUE				= "Brendan Bassett";
	}

	interface RATIO_F {

		Tag		TAG			= new Tag("RATIO");

		IntAttr	OCTAVES		= new IntAttr("octaves");
		IntAttr	NUMERATOR	= new IntAttr("numerator");
		IntAttr	DENOMINATOR	= new IntAttr("denominator");
	}

	interface RECT_F {

		Tag		TAG		= new Tag("RECT");

		IntAttr	LEFT	= new IntAttr("l");
		IntAttr	TOP		= new IntAttr("t");
		IntAttr	RIGHT	= new IntAttr("r");
		IntAttr	BOTTOM	= new IntAttr("b");
	}

	interface TONIC_BUTTON_F {

		Tag			TAG				= new Tag("TONIC_BUTTON");

		IntAttr		GRAPH_ID		= new IntAttr("graphID");
		IntAttr		OCTAVE_NUMBER	= new IntAttr("octaveNumber");
		FloatAttr	REL_P			= new FloatAttr("relP");
	}

	interface TIME_MARKER_F {

		Tag			TAG					= new Tag("TIME_MARKER");

		IntAttr		CONTAINING_GRAPH_ID	= new IntAttr("containingGraphID");
		FloatAttr	TIME				= new FloatAttr("time");
	}

	interface SCORE_WINDOW_F {

		Tag			TAG				= new Tag("WINDOW");

		Tag			GRAPH_BOUNDS	= new Tag("GRAPH_BOUNDS");

		FloatAttr	PPO				= new FloatAttr("ppo");
		FloatAttr	PPS				= new FloatAttr("pps");
	}

}
