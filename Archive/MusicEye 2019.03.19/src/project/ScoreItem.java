package project;

import gui.ScoreWindow;

import java.util.ArrayList;

import util.CustomException;
import util.L;

/**
 * Created by Brendan on 5/13/2015.
 *
 */

/**
 * @author Brendan
 *
 */
/**
 * @author Brendan
 *
 */
public abstract class ScoreItem {

	static public final int				NULL_ID		= -1;
	static public final ArrayList<Type>	TYPE_LIST	= new ArrayList<Type>();


	static private int					typeCounter	= 0;


	static public enum Type {

		GRAPH("GRAPH"),
		NOTE("NOTE"),
		OVERTONES("OVERTONES"),
		INTERVAL("INTERVAL"),
		TIME_MARKER("TIME_MARKER"),
		TONIC_BUTTON("TONIC_BUTTON"),
		PLAY_MARKER("PLAY_MARKER");

		// top-down == draw order :: bottom-up == action order

		public final int		orderIndex;
		protected final String	tag;


		Type(String _tag) {
			orderIndex = typeCounter++;
			tag = _tag;
			TYPE_LIST.add(this);
		}

		public String toString() {
			return tag;
		}

	}


	static public Type getByTag(String _tag) throws CustomException {
		for (int i = 0; i < TYPE_LIST.size(); i++) {
			Type type = TYPE_LIST.get(i);
			if (type.tag.matches(_tag)) {
				return type;
			}
		}
		throw new CustomException("none of type.tag matches _tag");
	}


	public final Project	project;
	public final Type		type;


	public ScoreItem(Type _type, Project _project) {
		type = _type;
		project = _project;
	}


	abstract protected Bounds createBounds(ScoreWindow _window);

	abstract public ScoreDrawable createDrawable(ScoreWindow _window);


	public void initAdd() {
		project.add(this);

		for (ScoreWindow window : project.windowList) {
			window.addBounds(createBounds(window));
		}
	}

	/**
	 * @param _newFocusedScoreItem 		gives focus to a specified ScoreItem. Can be null to remove focus from deleted item only
	 */
	public void invalidateRemove(ScoreItem _newFocusedScoreItem) {
		project.remove(this);

		L.l("ScoreItem.invalidateRemove()", "type==" + type.toString());

		for (ScoreWindow window : project.windowList) {
			window.invalidateBounds(this, _newFocusedScoreItem);
		}
	}

	/**
	 * resize just this bounds in all windows.
	 * AUTO REPAINT
	 * 
	 * @param _remakeDrawable Whether or not to remake all drawables from scratch.
	 */
	public void resizeBounds(boolean _remakeDrawable) {
		for (ScoreWindow window : project.windowList) {
			window.resizeBounds(this, _remakeDrawable);
		}
	}

}
