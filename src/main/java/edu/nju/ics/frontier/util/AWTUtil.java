package edu.nju.ics.frontier.util;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static java.awt.Frame.*;
import static java.awt.event.KeyEvent.*;
import static java.awt.event.MouseEvent.*;
import static java.awt.event.WindowEvent.*;

public class AWTUtil {
    /**
     * get the text description of a key event id
     * @param id id of a key event
     * @return the text description of the id
     */
    public static String getKeyId(int id) {
        switch (id) {
            case KEY_PRESSED:
                return "KEY_PRESSED";
            case KEY_RELEASED:
                return "KEY_RELEASED";
            case KEY_TYPED:
                return "KEY_TYPED";
            default:
                return null;
        }
    }

    /**
     * get the fuzzy key character.
     * The detailed values for the alphabetic, numeric and punctuation keys are omitted.
     * @param keyCode code of a key event
     * @return the fuzzy key character
     */
    public static String getKeyText(int keyCode) {
//        if (keyCode >= VK_0 && keyCode <= VK_9 ||
//                keyCode >= VK_A && keyCode <= VK_Z) {
//            return String.valueOf((char)keyCode);
//        }

        if (keyCode >= VK_A && keyCode <= VK_Z) {
            return "Letter";
        }

        if (keyCode >= VK_0 && keyCode <= VK_9) {
            return "Number";
        }

        if (keyCode >= VK_NUMPAD0 && keyCode <= VK_NUMPAD9) {
            return "Number";
        }

        switch(keyCode) {
            case VK_ENTER: return "Enter";
            case VK_BACK_SPACE: return "Backspace";
            case VK_TAB: return "Tab";
            case VK_CANCEL: return "Cancel";
            case VK_CLEAR: return "Clear";
            case VK_COMPOSE: return "Compose";
            case VK_PAUSE: return "Pause";
            case VK_CAPS_LOCK: return "Caps Lock";
            case VK_ESCAPE: return "Escape";
            case VK_SPACE: return "Space";
            case VK_PAGE_UP: return "Page Up";
            case VK_PAGE_DOWN: return "Page Down";
            case VK_END: return "End";
            case VK_HOME: return "Home";
            case VK_LEFT: return "Left";
            case VK_UP: return "Up";
            case VK_RIGHT: return "Right";
            case VK_DOWN: return "Down";
            case VK_BEGIN: return "Begin";

            // modifiers
            case VK_SHIFT: return "Shift";
            case VK_CONTROL: return "Control";
            case VK_ALT: return "Alt";
            case VK_META: return "Meta";
            case VK_ALT_GRAPH: return "Alt Graph";

            // punctuation
            case VK_COMMA: return "Punctuation";//"Comma";
            case VK_PERIOD: return "Punctuation";//"Period";
            case VK_SLASH: return "Punctuation";//"Slash";
            case VK_SEMICOLON: return "Punctuation";//"Semicolon";
            case VK_EQUALS: return "Punctuation";//"Equals";
            case VK_OPEN_BRACKET: return "Punctuation";//"Open Bracket";
            case VK_BACK_SLASH: return "Punctuation";//"Back Slash";
            case VK_CLOSE_BRACKET: return "Punctuation";//"Close Bracket";

            // numpad numeric keys handled below
            case VK_MULTIPLY: return "Punctuation";//"NumPad *";
            case VK_ADD: return "Punctuation";//"NumPad +";
            case VK_SEPARATOR: return "Punctuation";//"NumPad ,";
            case VK_SUBTRACT: return "Punctuation";//"NumPad -";
            case VK_DECIMAL: return "Punctuation";//"NumPad .";
            case VK_DIVIDE: return "Punctuation";//"NumPad /";
            case VK_DELETE: return "Delete";
            case VK_NUM_LOCK: return "Num Lock";
            case VK_SCROLL_LOCK: return "Scroll Lock";

            case VK_WINDOWS: return "Windows";
            case VK_CONTEXT_MENU: return "Context Menu";

            case VK_F1: return "F1";
            case VK_F2: return "F2";
            case VK_F3: return "F3";
            case VK_F4: return "F4";
            case VK_F5: return "F5";
            case VK_F6: return "F6";
            case VK_F7: return "F7";
            case VK_F8: return "F8";
            case VK_F9: return "F9";
            case VK_F10: return "F10";
            case VK_F11: return "F11";
            case VK_F12: return "F12";
            case VK_F13: return "F13";
            case VK_F14: return "F14";
            case VK_F15: return "F15";
            case VK_F16: return "F16";
            case VK_F17: return "F17";
            case VK_F18: return "F18";
            case VK_F19: return "F19";
            case VK_F20: return "F20";
            case VK_F21: return "F21";
            case VK_F22: return "F22";
            case VK_F23: return "F23";
            case VK_F24: return "F24";

            case VK_PRINTSCREEN: return "Print Screen";
            case VK_INSERT: return "Insert";
            case VK_HELP: return "Help";
            case VK_BACK_QUOTE: return "Punctuation";//"Back Quote";
            case VK_QUOTE: return "Punctuation";//"Quote";

            case VK_KP_UP: return "Up";
            case VK_KP_DOWN: return "Down";
            case VK_KP_LEFT: return "Left";
            case VK_KP_RIGHT: return "Right";

            case VK_DEAD_GRAVE: return "Dead Grave";
            case VK_DEAD_ACUTE: return "Dead Acute";
            case VK_DEAD_CIRCUMFLEX: return "Dead Circumflex";
            case VK_DEAD_TILDE: return "Dead Tilde";
            case VK_DEAD_MACRON: return "Dead Macron";
            case VK_DEAD_BREVE: return "Dead Breve";
            case VK_DEAD_ABOVEDOT: return "Dead Above Dot";
            case VK_DEAD_DIAERESIS: return "Dead Diaeresis";
            case VK_DEAD_ABOVERING: return "Dead Above Ring";
            case VK_DEAD_DOUBLEACUTE: return "Dead Double Acute";
            case VK_DEAD_CARON: return "Dead Caron";
            case VK_DEAD_CEDILLA: return "Dead Cedilla";
            case VK_DEAD_OGONEK: return "Dead Ogonek";
            case VK_DEAD_IOTA: return "Dead Iota";
            case VK_DEAD_VOICED_SOUND: return "Dead Voiced Sound";
            case VK_DEAD_SEMIVOICED_SOUND: return "Dead Semivoiced Sound";

            case VK_AMPERSAND: return "Ampersand";
            case VK_ASTERISK: return "Asterisk";
            case VK_QUOTEDBL: return "Double Quote";
            case VK_LESS: return "Less";
            case VK_GREATER: return "Greater";
            case VK_BRACELEFT: return "Left Brace";
            case VK_BRACERIGHT: return "Right Brace";
            case VK_AT: return "At";
            case VK_COLON: return "Colon";
            case VK_CIRCUMFLEX: return "Circumflex";
            case VK_DOLLAR: return "Dollar";
            case VK_EURO_SIGN: return "Euro";
            case VK_EXCLAMATION_MARK: return "Exclamation Mark";
            case VK_INVERTED_EXCLAMATION_MARK: return "Inverted Exclamation Mark";
            case VK_LEFT_PARENTHESIS: return "Left Parenthesis";
            case VK_NUMBER_SIGN: return "Number Sign";
            case VK_MINUS: return "Punctuation";//"Minus";
            case VK_PLUS: return "Punctuation";//"Plus";
            case VK_RIGHT_PARENTHESIS: return "Right Parenthesis";
            case VK_UNDERSCORE: return "Underscore";

            case VK_FINAL: return "Final";
            case VK_CONVERT: return "Convert";
            case VK_NONCONVERT: return "No Convert";
            case VK_ACCEPT: return "Accept";
            case VK_MODECHANGE: return "Mode Change";
            case VK_KANA: return "Kana";
            case VK_KANJI: return "Kanji";
            case VK_ALPHANUMERIC: return "Alphanumeric";
            case VK_KATAKANA: return "Katakana";
            case VK_HIRAGANA: return "Hiragana";
            case VK_FULL_WIDTH: return "Full-Width";
            case VK_HALF_WIDTH: return "Half-Width";
            case VK_ROMAN_CHARACTERS: return "Roman Characters";
            case VK_ALL_CANDIDATES: return "All Candidates";
            case VK_PREVIOUS_CANDIDATE: return "Previous Candidate";
            case VK_CODE_INPUT: return "Code Input";
            case VK_JAPANESE_KATAKANA: return "Japanese Katakana";
            case VK_JAPANESE_HIRAGANA: return "Japanese Hiragana";
            case VK_JAPANESE_ROMAN: return "Japanese Roman";
            case VK_KANA_LOCK: return "Kana Lock";
            case VK_INPUT_METHOD_ON_OFF: return "Input Method On/Off";

            case VK_AGAIN: return "Again";
            case VK_UNDO: return "Undo";
            case VK_COPY: return "Copy";
            case VK_PASTE: return "Paste";
            case VK_CUT: return "Cut";
            case VK_FIND: return "Find";
            case VK_PROPS: return "Props";
            case VK_STOP: return "Stop";
        }

        if ((keyCode & 0x01000000) != 0) {
            return getKeyText(keyCode ^ 0x01000000);
        }
        return null;
    }

    /**
     * get the text character of a key event.
     * @param keyChar character of a key event
     * @return the text character of the key event
     */
    public static String getKeyChar(char keyChar) {
        switch (keyChar) {
            case '\b':
                return getKeyText(VK_BACK_SPACE);
            case '\t':
                return getKeyText(VK_TAB);
            case '\n':
                return getKeyText(VK_ENTER);
            case '\u0018':
                return getKeyText(VK_CANCEL);
            case '\u001b':
                return getKeyText(VK_ESCAPE);
            case '\u007f':
                return getKeyText(VK_DELETE);
            case CHAR_UNDEFINED:
                return null;
            default:
                return "'" + keyChar + "'";
        }
    }

    /**
     * get the text description of a key event's modifiers
     * @param modifiers a key event's modifiers
     * @return the text description of the key event's modifiers
     */
    public static String getKeyModifiers(int modifiers) {
        String s = null;
        if (modifiers != 0) {
            s = KeyEvent.getKeyModifiersText(modifiers);
        }
        return (s == null || s.length() <= 0) ? null : s;
    }

    /**
     * get the text description of a mouse event id
     * @param id a mouse event id
     * @return the text description of the mouse event id
     */
    public static String getMouseId(int id) {
        switch(id) {
            case MOUSE_PRESSED:
                return "MOUSE_PRESSED";
            case MOUSE_RELEASED:
                return "MOUSE_RELEASED";
            case MOUSE_CLICKED:
                return "MOUSE_CLICKED";
            case MOUSE_ENTERED:
                return "MOUSE_ENTERED";
            case MOUSE_EXITED:
                return "MOUSE_EXITED";
            case MOUSE_MOVED:
                return "MOUSE_MOVED";
            case MOUSE_DRAGGED:
                return "MOUSE_DRAGGED";
            case MOUSE_WHEEL:
                return "MOUSE_WHEEL";
            default:
                return null;
        }
    }

    /**
     * get the format string of the absolute and relative coordinates of a mouse event
     * @param absX absolute x
     * @param absY absolute y
     * @param x relative x
     * @param y relative y
     * @return the format string of the coordinates of the mouse event
     */
    public static String getMouseCoordinates(int absX, int absY, int x, int y) {
        return String.format("(%d,%d)/(%d,%d)", absX, absY, x, y);
    }

    /**
     * get the text description of a mouse event's button
     * @param id mouse event id
     * @param button mouse event's button
     * @return the text description of the mouse event's button
     */
    public static String getMouseButton(int id, int button) {
        if (button != NOBUTTON && id != MOUSE_DRAGGED && id != MOUSE_MOVED){
            return String.valueOf(button);
        } else {
            return null;
        }
    }

    /**
     * get the text description of a mouse event's modifiers
     * @param modifiers a mouse event's modifiers
     * @return the text description of the mouse event's modifiers
     */
    public static String getMouseModifiers(int modifiers) {
        String s = null;
        if (modifiers != 0) {
            s = MouseEvent.getMouseModifiersText(modifiers);
        }
        return (s == null || s.length() <= 0) ? null : s;
    }

    /**
     * get the text description of a mouse event's click count
     * @param clickCount a mouse event's click count
     * @return the text description of the mouse event's click count
     */
    public static String getMouseClickCount(int clickCount) {
        return (clickCount > 0) ? String.valueOf(clickCount) : null;
    }

    /**
     * get the text description of a window event's id
     * @param id a window event's id
     * @return the text description of the window event's id
     */
    public static String getWindowId(int id) {
        switch(id) {
            case WINDOW_OPENED:
                return "WINDOW_OPENED";
            case WINDOW_CLOSING:
                return "WINDOW_CLOSING";
            case WINDOW_CLOSED:
                return "WINDOW_CLOSED";
            case WINDOW_ICONIFIED:
                return "WINDOW_ICONIFIED";
            case WINDOW_DEICONIFIED:
                return "WINDOW_DEICONIFIED";
            case WINDOW_ACTIVATED:
                return "WINDOW_ACTIVATED";
            case WINDOW_DEACTIVATED:
                return "WINDOW_DEACTIVATED";
            case WINDOW_GAINED_FOCUS:
                return "WINDOW_GAINED_FOCUS";
            case WINDOW_LOST_FOCUS:
                return "WINDOW_LOST_FOCUS";
            case WINDOW_STATE_CHANGED:
                return "WINDOW_STATE_CHANGED";
            default:
                return null;
        }
    }

    /**
     * get the text description of a window's state
     * @param state a window's state
     * @return the text description of the window's state
     */
    public static String getWindowState(int state) {
        switch (state) {
            case NORMAL:
                return "NORMAL";
            case ICONIFIED:
                return "ICONIFIED";
            case MAXIMIZED_HORIZ:
                return "MAXIMIZED_HORIZ";
            case MAXIMIZED_VERT:
                return "MAXIMIZED_VERT";
            case MAXIMIZED_BOTH:
                return "MAXIMIZED_BOTH";
            default:
                return null;
        }
    }

    /**
     * get the name and type of a window
     * @param window a window
     * @return the name and type of the window
     */
    public static String getWindowInfo(Window window) {
        if (window == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(window.getName());
        Type type = window.getType();
        if (type != null) {
            builder.append(",").append(type.name());
        }
        return builder.toString();
    }
}
