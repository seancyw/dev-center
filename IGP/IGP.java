#define USE_NEW_RESOURCE_IGP
#define USE_FONT_SPRITE
#define getFontMetric(character, metricFlag) 0
/**
 * IGP class - the InGamePromotion module v 2.2
 */
import java.io.InputStream;

//#ifdef BLACKBERRY
//@import net.rim.device.api.ui.UiApplication;
//@import net.rim.device.api.ui.Font;
//@import net.rim.device.api.ui.container.FullScreen;
//@import net.rim.device.api.system.ApplicationDescriptor;
//@import javax.microedition.lcdui.game.Sprite;
//#else
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.game.Sprite;
//#endif

//#if BLACKBERRY || BLACKBERRY_MIDP
//@import net.rim.blackberry.api.browser.Browser;
//@import net.rim.blackberry.api.browser.BrowserSession;
//@import net.rim.device.api.servicebook.ServiceBook;
//@import net.rim.device.api.servicebook.ServiceRecord;
//@import java.io.EOFException;
//@import net.rim.device.api.synchronization.ConverterUtilities;
//@import net.rim.device.api.util.DataBuffer;
//@import net.rim.device.api.compress.GZIPInputStream;
//@import javax.microedition.lcdui.game.Sprite;
//#endif

import javax.microedition.rms.RecordStore;

//#ifdef ANDROID
//@import com.gameloft.android.wrapper.Utils;
//#endif

//#ifdef BLACKBERRY
//@public class IGP implements Runnable
//#else
public class IGP implements Runnable, CommandListener
//#endif
{
	#ifdef USE_NEW_RESOURCE_IGP //xuan		
		private static String[] _AvailableLanguages = {"EN"};		
		private static String[] __PROMOS = {"BLB3","BRC3","UNOG"};
		private static String[] __WNList = {"LPL2","BUB2","MPL2","LBOW","WN"};
		private static String[] __BSList = {"MHM2","ASP4","DITW","RF11","BS"};
		private static String[] __CatalogList = {"WN","BS","PROMO","ZVIP","OP"};		
		private static boolean[]  __optionalFeatures = {false};
	#endif
	public static boolean m_IsInstall_First = false;
	
	public static String CODE_VERSION = "2.2";
	public static String signature = "IGP-Signature=" + CODE_VERSION;
	private static String DATA_VERSION = "";
    
	// Debug
    private final static String         IGP_DEBUG_HEADER                = ">> IGP: ";
	private final static int            IGP_DEBUG_TAB_SPACING           = 4;
	private static int                  currentDebugSpacing             = 0;

	// resource loading options
	public final static int LOAD_RESOURCES_ON_START = 0;
	public final static int LOAD_RESOURCES_ON_PAGE_CHANGE = 1;

	// platformRequest invocation method
	public final static int PLATFORM_REQUEST_ON_PAINT = 1;
	public final static int PLATFORM_REQUEST_ON_NEW_THREAD = 2;

	// this variable contains the URL that the IGPs requested
	// that the application must pass to platformRequest
	public static String URLPlatformRequest;

	// These actions are the ones that you should pass to updateIGPs
	// based on the device's keypresses
	public final static int ACTION_NONE = 0;
	public final static int ACTION_UP = 21;
	public final static int ACTION_DOWN = 32;
	public final static int ACTION_LEFT = 23;
	public final static int ACTION_RIGHT = 24;
	public final static int ACTION_SELECT = 25;
	public final static int ACTION_BACK = 26;
	public final static int ACTION_SELECT_BOX = 27;

	//Main Menu Entry Constants
	public final static int MENU_NONE = -1;
	public final static int MENU_MORE_GAMES = 0;
	public final static int MENU_FREE_CHAT = 1;
	public final static int MENU_MORE_GAMES_AND_FREE_CHAT = 2;

	// is true if initialize() realizes that this game has Orange IGPs
	private static boolean bOrangeCatalog;

	//#ifdef ENABLE_VODA
//@	private static boolean bVodafoneCustomization; // Special Offers customized with Vodafone
	//#endif	

	// IGP internal states
	public final static int 			STATE_LOADING 					= 0;
	public final static int 			STATE_PAGE 						= 1;
	public final static int 			STATE_INTERPAGE_LOADING 		= 2;
	public final static int 			STATE_ASKING_TO_CLOSE_APP 		= 3;
	public final static int				STATE_EXIT_IGP 					= 4;
	public final static int 			STATE_INTERRUPTED 				= 5;
	public final static int 			STATE_PLATFORM_REQUEST 			= 6;
	public final static int				STATE_ZVIP						= 7;

	private static Font 				sysFont;

	// IGP jad attributes
	private final static String 		URL_PREFIX 						= "URL";
	private final static String 		IGP_PREFIX 						= "IGP";

	// IGP data file name
	private final static String 		IGP_DATA_FILE 					= "/dataIGP";

	// Display constants
	private static int 					BOX_COLOR						= 0xCF0900;
	private final static int 			BK_COLOR 						= 0xFFFFFF;
	private final static int 			GLIVE_BLUE 						= 0x0099FF;
	private final static int			ASK_BKG_COLOR					= 0xDFDFDF;
	private final static int			ASK_TRANS_BKG_COLOR				= 0xF2DFDFDF;
	//private final static int 			GRADIENT_TOP 					= 0x03141C;
	//private final static int 			GRADIENT_CENTER 				= 0x0088D7;	
	//private final static int 			GRADIENT_PROMOTION_TOP 			= 0x03141C;//0xAA6600;
	//private final static int 			GRADIENT_PROMOTION_CENTER 		= 0xb6cdd8;//0xF4F201;
	private final static int 			GRADIENT_BOTTOM					= 0x6E94A8;
	private final static int			HEADER_SEPARATION_LINE_COLOR	= 0xa5becb;
	private final static int 			LIST_ARROW_SCROLL_COLOR 		= 0xffffff;
	private final static int 			LIST_ARROW_BORDER_COLOR 		= 0xFC7704;
	private final static int 			LIST_SELECTION_COLOR 			= 0xFC7704;
	private final static int 			SIDE_ARROWS_ANIMATION_WIDTH 	= 8;
	private final static int 			ARROW_ANIM_DELAY 				= 80;
	private final static int 			BLINK_TIME 						= 1000;
	 //Special Offers constants
	 private final static int			CORD_OFFSET_X_176	= 6;
	 private final static int			CORD_OFFSET_Y_176 	= 10;
	 private final static int			CORD_WIDTH_176		= 1;
	 private final static int			CORD_COLOUR_176		= 0xCDCDCD;
	 
	 private final static int			CORD_OFFSET_X_240	= 8;
	 private final static int			CORD_OFFSET_Y_240 	= 11;
	 private final static int			CORD_WIDTH_240		= 2;
	 private final static int			CORD_COLOUR_240		= 0xD7D7D7;
	 
	 private final static int			CORD_OFFSET_X_320	= 14;
	 private final static int			CORD_OFFSET_Y_320 	= 20;
	 private final static int			CORD_WIDTH_320		= 3;
	 private final static int			CORD_COLOUR_320		= 0xCCCCCC;

	// Softkeys Coordinates
	private static int 					IGP_LSK_X 						= IGPConfig.leftSoftkeyXOffsetFromBorders;
    private static int 					IGP_RSK_X 						= IGPConfig.rightSoftkeyXOffsetFromBorders;
	private final static int 			IGP_SK_Y_offset 				= 2;
    private static int                  SOFTKEYS_AREA_TOP;

	// Items Y Cooridnates
	private static int					GAMELOFT_LOGO_Y;
	private static int					PRESS5_TO_GETIT_Y;
	private static int					PRESS5_TO_VISIT_Y;
    private static int                  HEADER_BOTTOM = 0;
    

    private static int 					DEFAULT_CHAR_SPACING 			= 0;
    private final static int			DEFAULT_LINE_SPACING 			= 0;

	// Progress Bar
	private final static int 			PROGRESSBAR_BORDER_COLOR 		= 0xFFFFFF;
	private final static int 			PROGRESSBAR_FILL_COLOR 			= 0xFF0000;
	private final static int 			PROGRESSBAR_FILL_HEIGHT 		= 3;
	private final static int 			PROGRESSBAR_BORDER_SPACING 		= 1;
	private final static int 			PROGRESSBAR_BORDER_HEIGHT 		= PROGRESSBAR_FILL_HEIGHT + (2 * PROGRESSBAR_BORDER_SPACING) + 1;

	// Optional features (this chunks are haven't got static indexes
	//#ifdef ENABLE_GLIVE	
//@	private final static int            OPTIONAL_FEATURE_GLLIVE         = 0;
//@	private final static int            OPTIONAL_FEATURE_ORANGE_GLOBAL  = 1;
	//#ifdef ENABLE_VODA	
//@	private final static int            OPTIONAL_FEATURE_VODAFONE       = 2;
//@	private final static int            OPTIONAL_FEATURES_COUNT         = 3;
	//#else	
//@	private final static int            OPTIONAL_FEATURES_COUNT         = 2;
	//#endif	
	//#else
	private final static int            OPTIONAL_FEATURE_ORANGE_GLOBAL  = 0;
	//#ifdef ENABLE_VODA
//@	private final static int            OPTIONAL_FEATURE_VODAFONE       = 1;
//@	private final static int            OPTIONAL_FEATURES_COUNT         = 2;
	//#else
	private final static int            OPTIONAL_FEATURES_COUNT         = 1;
	//#endif
	//#endif

	private static boolean[]            optionalFeatures                = new boolean[OPTIONAL_FEATURES_COUNT];

#ifdef USE_FONT_SPRITE
	private static ASprite _fontSprite;	
#endif

	// IGP generic type - for data chunks, loading stes, pages
	private final static int 			IGP_TYPE_START         			= -1;
	private final static int 			IGP_TYPE_LIBOPEN       			= 0;
	private final static int 			IGP_TYPE_TEXTS         			= 1;
	private final static int 			IGP_TYPE_FONT          			= 2;
	private final static int 			IGP_TYPE_GLOBAL        			= 3;
	private final static int 			IGP_TYPE_PROMOS       			= 4;
	private final static int 			IGP_TYPE_PROMOTION     			= 5;
	private final static int 			IGP_TYPE_WN            			= 6;
	private final static int 			IGP_TYPE_BS            			= 7;
	private final static int 			IGP_TYPE_CATALOG     			= 8;

	private final static int            IGP_TYPE_LAST_STATIC_INDEX      = IGP_TYPE_CATALOG;
	private static int[]                IGP_TYPE_OPTIONAL_FEATURES      = new int[OPTIONAL_FEATURES_COUNT];

	private static int                  IGP_TYPE_END;

	public static int                   firstLoadStep                   = IGP_TYPE_START;
	public static int                   lastLoadStep                    = IGP_TYPE_LAST_STATIC_INDEX;

	// IGP Loading steps
	private final static int 			NB_LOAD_STEPS_GENERIC 			= 4; // (chunkHeader + chunkText + chunkFont + chunkGlobalRes)
	private final static int            LOAD_FIRST_PROMO 				= NB_LOAD_STEPS_GENERIC + 1;

	// IGP pages
	private static int                  PAGE_PROMOTION;
	private static int                  PAGE_WN;
	private static int                  PAGE_BS;
	private static int                  PAGE_OPERATOR;
	private static int                  PAGE_GLIVE;
	private static int                  PAGE_ORANGE;

	// Softkey Paint
	private final static int 			DISPLAY_SELECT_SK 				= 1;
	private final static int 			DISPLAY_BACK_SK 				= 1 << 1;
	private final static int 			DISPLAY_NONE_SK 				= 1 << 2;

	private final static int 			DISPLAY_LEFT_BACKGROUND_SK		= 1;
	private final static int 			DISPLAY_RIGHT_BACKGROUND_SK		= 1 << 1;
	private final static int 			DISPLAY_NONE_BACKGROUND_SK		= 1 << 2;

	private static int 					IGP_TEXT_CATALOG_ORANGE;
	private static int 					IGP_TEXT_VISIT_YOUR_PORTAL;
	private static int 					IGP_TEXT_PRESS_5_TO_VISIT;
	private static int 					IGP_TEXT_PRESS_5_TO_GET_IT;
	private static int 					IGP_TEXT_PRESS_OK_TO_VISIT;
	private static int 					IGP_TEXT_PRESS_OK_TO_GET_IT;
	private static int 					IGP_TEXT_SELECT;
	private static int 					IGP_TEXT_BACK;
	private static int 					IGP_TEXT_TOUCHVISIT;
	private static int 					IGP_TEXT_TOUCHGET;
	private static int 					IGP_TEXT_DPADVISIT;
	private static int 					IGP_TEXT_DPADGET;
	//#ifdef ENABLE_GLIVE	
//@	private static int 					IGP_TEXT_GLIVE_1;
//@	private static int 					IGP_TEXT_GLIVE_2;
//@	private static int 					IGP_TEXT_GLIVE_FREECHAT;
	//#endif	
	private static int 					IGP_EXIT_CONFIRM;
	private static int 					IGP_TEXT_WN_GAME_0;
    private static int                  IGP_TEXT_MORE_WN;
	private static int 					IGP_TEXT_BS_GAME_0;
    private static int                  IGP_TEXT_MORE_BS;
	private static int 					IGP_TEXT_COMPLETE_CATALOGUE;
    private static int 					IGP_TEXT_LOADING;

	//#ifdef ENABLE_VODA	
//@	private static int 					IGP_TEXT_5_VODAFONE;
//@	private static int 					IGP_TEXT_TOUCH_VODAFONE;
	//#endif	

	// list
	private final static int 			WN_LIST 						= 0;
	private final static int			BS_LIST 						= 1;
	private final static int 			COMPLETE_CATALOG_LIST			= 2;
	private final static int 			NUMBER_OF_LIST 					= 3;

	// Font Colors
	private final static int 			WHITE_FONT 						= 0;
	private final static int 			BLUE_FONT 						= 1;
	private final static int 			RED_FONT 						= 2;
	private final static int 			DARK_FONT						= 3;
	private final static int 			DARK_BLUE_FONT					= 4;
	private final static int 			BLUE_GRAY_FONT					= 5;
	private final static int			DARK_ORANGE_FONT						= 6;
	private final static int 			NB_FONT_COLORS					= 7;
	
	// Colors
	private final static int 			BLACK 							= 0;
	private final static int			DARK_RED						= 1;
	private final static int			PURPLE							= 2;
	private final static int 			LIGHT_BROWN						= 3;
	

	// config constants
	private final static String 		IGP_GAME_WN 					= "WN";
	private final static String 		IGP_GAME_BS 					= "BS";
	private final static String 		IGP_CATALOG 					= "CATALOG";
	private final static String 		IGP_GLDT 						= "GLDT";
	//#ifdef ENABLE_VODA	
//@	private final static String 		IGP_VODAFONE 					= "VDF";
	//#endif	
    private final static String         IGP_ZVIP                        = "ZVIP";
	private final static String 		IGP_PROMOTION 					= "PROMO";
	private final static String 		IGP_PT 							= "URL-PT";
	private final static String 		URL_OPERATOR 					= "URL-OPERATOR";

	private final static int 			MAX_URL_PT_LINES 				= 3;

	// Data file structure defines
	private final static int 			IMG_4BLUE 						= 0;
	private final static int 			IMG_4WHITE 						= 0;
	private final static int 			IMG_6BLUE 						= 0;
	private final static int 			IMG_6WHITE 						= 0;
	private final static int 			IMG_LEFTBLUE 					= 0;
	private final static int 			IMG_LEFTWHITE 					= 0;
	private final static int 			IMG_RIGHTBLUE 					= 0;
	private final static int 			IMG_RIGHTWHITE 					= 0;
	private final static int			IMG_BACK 						= 0;
	private final static int 			IMG_TICK 						= 0;
	private final static int 			IMG_GAMELOFT 					= 0;
	private final static int 			IMG_MORE 						= 0;   
	private final static int 			IMG_BUTTON_WHITE 				= 0;
	private final static int 			IMG_BUTTON_BLUE 				= 0;
    private final static int            IMG_ROUND_CORNER                = 0;

	private static int 					PROMOTION_CHUNK_ID 				= -1;
	private static int 					DATA_IMG_COUNT					= IMG_ROUND_CORNER + 1;

	// ---------------------------------------------------------------
	// Lib File Format:
	// size(bytes) description
	// ----------------------------------------------------------
	// 2 -> nf = number of chunks
	// 4 * nf -> offset for each chunk
	// ... -> data for each chunk (last chunk must be an empty one)
	// ---------------------------------------------------------------
	private static int _lib_nChunks; // number of chunks
	private static int[] _lib_pOffset; // offset of each chunk
	private static byte[][] _lib_pData; // cache data for each chunk

	// ---------------------------------------------------------------
	// Array parsing methods
	// ---------------------------------------------------------------
	private static int dataOffset;

	// cp will store x, y, w, h of every letter in the font
	//private static final double PI = 3.14159265358979323846;
	private static byte[] fontDescriptor;
	private static int fontHeight;
	private static int FONT_X = 0;
	private static int FONT_Y = 1;
	private static int FONT_W = 2;
	private static int FONT_H = 3;
	private static Image[] fontImage;
	private static int drawColor;
	private final static int MAX_NB_LINES = 10;
	private final static int MAX_NB_LINES_ON_WN_BS = 2;
	private final static byte lineSeparator = '\n';
	private static int[] s_line_w;
	private static int s_text_h;
	private static int s_text_w;
	private static boolean bDetermineSizeOnly;
	private final static byte k_Check_NULL = (1 << 0);
	private final static byte k_Check_DEL = (1 << 1);
	private final static byte k_Check_NO = (1 << 2);
	private final static byte k_Check_ALL = k_Check_DEL | k_Check_NULL | k_Check_NO;
	private final static int LOADING_INTER_SPACE = 5;

	//	private final static byte k_Check_PROMO = k_Check_DEL;
	private final static byte k_Check_PROMO = k_Check_ALL;

	//	private final static byte k_Check_DEMO = k_Check_ALL;
	private final static byte k_Check_ORANGE = k_Check_DEL;
	private final static String[] PAGE_LIST_PREFIXES = { URL_PREFIX + "-" + IGP_GAME_WN, URL_PREFIX + "-" + IGP_GAME_BS, URL_PREFIX, };
	private final static String s_IGP_PROMOS = "IGP-PROMOS";

	//	private final static String s_IGP_DEMOS = "IGP-DEMOS";
	private final static String s_IGP_WN = "IGP-WN";
	private final static String s_IGP_BS = "IGP-BS";
	private final static String s_IGP_CATEGORIES = "IGP-CATEGORIES";
	private final static String k_URL_TEMPLATE_GAME = "URL-TEMPLATE-GAME";
	private final static String k_URL_OVI = "more_games_url";
	private final static String k_POWER_OVI = "more_games_status";
	private static String s_URL_TEMPLATE_OVI = null;
	private static String s_POWER_TEMPLATE_OVI = null;
	private static String s_POWER_DEFAULT_VALUE = "on";
	
	//#ifdef ENABLE_GLIVE	
//@	private final static String s_GLive = "URL-GLIVE";
	//#endif	
	private final static String s_PLACEHOLDER = "XXXX";
	private final static String s_ctg_PLACEHOLDER = "&ctg=" + s_PLACEHOLDER;
	private final static String s_CTG = "&ctg=";
	private final static String s_LANGUAGE = "&lg=";
	private final static String s_PROMO_SCREEN_HEADER = "SC";
	private final static String s_OPERATOR = "CCTL";
	private final static String s_IGP_REDIR_URL = "ingameads.gameloft.com/redir";
	private final static String s_GAME_SEPARATOR = ";";
	private static String s_URL_TEMPLATE_GAME;
	private static boolean b_useIGPRedir;
	private static boolean b_useRedir;

	//#ifdef BLACKBERRY
//@	private static String s_igpPromosFake;
//@	private static String s_igpCategoriesFake = null;
//@	private static boolean s_jadNotFound = false;
	//#endif

	//#if BLACKBERRY || BLACKBERRY_MIDP	
//@	private static ServiceBook sb;
//@	private static ServiceRecord[] records;
	//#endif	

	// list arrays initialization
	public static String GameName;
	private static String operatorCode;
	private static String screenWidth;
	private static String screenHeight;
	private static boolean useLowDetail = false;
    private static boolean useLowDetailSplashes = false;
    private static boolean useTransformSpriteRegion = true;
    private static String genericsSize;
	public static String[] AvailableLanguages = new String[0];
	private static String[] WNList;
	private static String[] BSList;
	private static String[] CatalogList;
	private static int NumberOfPromos;
	private static int numberOfPages;
	private static String[] _StrMgr_Pack;
	private static short[] _StrMgr_Offsets;
	private static int x_ArrowNumberLeft = 6;
	private static int x_ArrowNumberRight = 3;

	// internal display vars
	private static int _arrowPressedCounter;
	private static boolean _redArrowLeft;
	private static boolean _redArrowRight;
	private static final String IGP_RECORDSTORE_NAME = "igp19";
	private static boolean wasVisited = false;
	private static boolean wasVisitedCached = false;
	private static boolean needRedraw = true;

	// convenience method to draw the "new" arrow. It takes care of whether it should be
	// drawn or not (if m_bHandleRecordstore is true), and handles the blinking.
	private final static int ARROW_BLINK_TIME = 800;
	private static long lastArrowUpdate = 0;
	private static boolean drawArrow = true;

	// midlet
	// game
	//#ifdef BLACKBERRY
//@	private static UiApplication MidletInstance;
//@	private static FullScreen GameInstance;
	//#else	
	private static MIDlet MidletInstance;
	private static Canvas GameInstance;
	//#endif

	private static String URLPlatformRequestPending;

	// use MIDP commands instead of graphic softkeys
	private static boolean useCommandBar = false;

	// Game Command Listener
	//#ifndef BLACKBERRY
	private static CommandListener GameCmdListener = null;
	//#endif

	public static IGP igpInstance = null;

	// used by IGP Server
	private static boolean IGPRunning = false;
	private static String IGPServerURL = null;

	// Global IGP variables
	private static boolean 				isAvailable 					= false;
	private static boolean 				isZVIPAvailable					= false;

	// State variables
	private static int 					currentState;
	private static int 					currentAction;
	private static int 					currentPage;
	private static int 					lastState;

	// Loading variables
	private static int currentLoadingImage;
	private static int CurrentLoadingStep;
	private static int TotalLoadingSteps;

	// Images
	private static Image[] GlobalImages;
	private static Image[] PageImages;
	private static Image SpOfText;
	private static Image completeCatalogText;
#ifndef USE_NEW_RESOURCE_IGP
	private static Image[] roundCorner;
#endif
	//#ifdef ENABLE_GLIVE	
//@	private static Image GLiveImage;
	//#endif	
	private static Image left_sk_img;
	private static Image right_sk_img;
//#if BLACKBERRY || BLACKBERRY_MIDP
//@	private static Image leftBB_sk_img;
//@	private static Image rightBB_sk_img;
//@	private static Image centerBB_sk_img;
//#endif
	private static Image sk_background_img;
	private static Image sk_background_blink_img;

	private static Image arrow_left_blink_img;
	private static Image arrow_right_blink_img;
	private static Image arrow_left_img;
	private static Image arrow_right_img;
	private static Image arrow_custom_left_img;
	private static Image arrow_custom_right_img;
	private static Image arrow_custom_left_blink_img;
	private static Image arrow_custom_right_blink_img;

	private static Image [] promosSplashesOverriden;
	private static Image imgGameloftLogo;
	private static Image imgBackBuffer;

	private static Image backGroundImage;

	// images lists
	private static Image[][] LIST_Images;

	// URLs lists
	private static String[][] LIST_URLs; // urls for each LIST item
	private static int[][] ValidLISTIdx;
	private static int[][] LIST_Texts;
	private static int[] LIST_nbItems;

	// list specific
	private static int currentList;
	private static int currentList_nbItems; // refference

	private static int LIST_visibleItemCount; // nb of visible items in the scrollable

	// list
	private static int s_igpListDisplayItemStart; // first item of the list to display
	private static int s_ItemListIndex; // selected item of the list
	private static int listSelector_index; // index of the list item selected
	private static int listSelector_x; // x postion of the list selector
	private static int listSelector_y; // y position of the list selector
	private static int listSelector_w; // width of the list selector
	private static int listSelector_h; // height of the list selector

	// Page Logic	
	private static int                 ValidRealPages = -1;
	private static String[]            pagesURLs;
	private static int[]               pagesType;
	private static boolean[]           pagesValid;
	private static int                 validPromos = 0;

	private static int pageTextId;
	private static boolean bDisplayButton;
	private static byte s_displaySoftKeysIcons;
	private static byte s_displaySoftKeysBackgrounds;
	private static int softkeysAdditionalSpace;
	private static boolean bIsListPage;
	private static int iButtonStringId;
	private static int box_w;
	private static int box_h;
	private static int box_x;
	private static int box_y;
	private static int CurrentLanguage;
    private static boolean canDrawHeader = true;

	// Images
	private static Image promoTextTwist;
	//#ifdef ENABLE_VODA	
//@	private static Image imgVodafoneLogo;
	//#endif	


	private static String s_textPt;
	private static String StringLoading;
	private static String s_urlOperator = "";
    private static String urlZVIP;
    private static String urlOVI;
    
    
    private static boolean useOVIFeature = false;
    
    
    private static int numberOfLinesPromoText;
	private static int longestLinePromoText;
	private static int IGP_SCRW;
	private static int IGP_SCRH;
	private static int IGP_HALF_SCRW;
	private static int IGP_HALF_SCRH;

	//#ifndef BLACKBERRY	
	private static Command cmdSelect;
	private static Command cmdBack;
	//#endif	

	// Tookie Pointers
	private static int 					lastTouchActionPressed		= ACTION_NONE;
	private static int 					lastTouchActionReleased		= ACTION_NONE;
	private static boolean 				isPointerReleased			= false;
	private static boolean 				isOKSoftkeyBeenTouched		= false;
	private static boolean 				isDragging                  = false;
	private static int 					ItemListBeforeDrag          = 0;

	// Used for bigger touch areas with IGPConfig.useBiggerTouchAreas flag
	private static int 					TOUCH_AREA_X 				= -1;
	private static int 					TOUCH_AREA_Y 				= -1;
	private static int 					TOUCH_AREA_W 				= -1;
	private static int 					TOUCH_AREA_H 				= -1;



	static int x_pointer = -1;
	static int y_pointer = -1;

	private static int Y_TouchPosition;
	private static int Y_littleArrow_Up;
	private static int Y_littleArrow_Down;
	//#ifdef ENABLE_GLIVE	
//@	private static String GLiveURL;
//@	//private static boolean IsGLiveAvailable;
	//#endif

	//used when useHardcodedLinks is activated
	private static java.util.Hashtable s_jadTable = new java.util.Hashtable();

	private static boolean enterZVIP = false;

	private static int fontCoordBytesFixedOffset = 0;
	// Read the dataIGP and get chunks information
	
	private final static int GLOBAL_IMAGE_ARROW_LEFT_N 		= 0;
	private final static int GLOBAL_IMAGE_ARROW_LEFT_H 		= 1;
	private final static int GLOBAL_IMAGE_ARROW_RIGHT_N 	= 2;
	private final static int GLOBAL_IMAGE_ARROW_RIGHT_H 	= 3;
	private final static int GLOBAL_IMAGE_VISIT 			= 4;
	private final static int GLOBAL_IMAGE_VISIT_V 			= 5;
	private final static int GLOBAL_IMAGE_SELECTION 		= 6;
	private final static int GLOBAL_IMAGE_TOTAL 			= 7;
	
	private static boolean openDataIGP()
	{
	#ifdef USE_NEW_RESOURCE_IGP //xuan
		return true;
	#else
		closeDataIGP();

		try
		{
			InputStream is = null;
            is = getResourceAsStreamIGP(IGP_DATA_FILE);		

			// Number of chunks...
			_lib_nChunks = (is.read() & 0xFF);
			_lib_nChunks += ((is.read() & 0xFF) << 8);

			// Offset of each chunk...
			_lib_pOffset = new int[_lib_nChunks];

			for (int i = 0; i < _lib_nChunks; i++)
			{
				_lib_pOffset[i] = (is.read() & 0xFF);
				_lib_pOffset[i] += ((is.read() & 0xFF) << 8);
				_lib_pOffset[i] += ((is.read() & 0xFF) << 16);
				_lib_pOffset[i] += ((is.read() & 0xFF) << 24);
			}
			is.close();
			is = null;
		}
		//#ifdef BLACKBERRY		
//@		catch (Throwable e)
			//#else			
			catch (Exception e)
			//#endif		
			{
				e = null;
				return false;
			}

		return true;
	#endif
	}

	private static void closeDataIGP()
	{
	#ifndef USE_NEW_RESOURCE_IGP //xuan
		_lib_pOffset = null;
		_lib_pData = null;
		_lib_nChunks = 0;
		System.gc();
	#endif
	}

	private static byte[] readDataIGP(int index)
	{
		
		if ((index < 0) || (index >= (_lib_nChunks - 1)))
		{
			return null;
		}

		System.out.println("------------------------private static byte[] readDataIGP(int index)");
		int chunk_size = _lib_pOffset[index + 1] - _lib_pOffset[index];

		if (chunk_size == 0)
		{
			return null;
		}

		if (_lib_pData != null)
		{
			return _lib_pData[index];
		}
		else
		{
			byte[] data = null;

			try
			{
				System.out.println("------------------------Read file: "+IGP_DATA_FILE);
				InputStream is = null;
				is = getResourceAsStreamIGP(IGP_DATA_FILE);
				is.skip(2 + (4 * _lib_nChunks) + _lib_pOffset[index]);
				data = new byte[chunk_size];
				int toRead = data.length;
				while(toRead > 0)
				{
					toRead -= is.read(data);
				}

				is.close();
				System.out.println("------------------------Read file: "+IGP_DATA_FILE +"is ok");
				is = null;
			}
			//#ifdef BLACKBERRY		
//@			catch (Throwable e)
				//#else			
				catch (Exception e)
				//#endif
				{
					//#ifdef DEBUG
//@					e.printStackTrace();
					//#else
					e = null;
					//#endif
				}

			return data;
		}
	}

	private static int readInt(byte[] data)
	{
		return (data[dataOffset++] & 0xFF) + ((data[dataOffset++] & 0xFF) << 8);
	}

	// Returns an image in a byte array skiping the image size
	private static byte[] getImageBytes(byte[] data)
	{
		//#ifdef DEBUG
//@			if ((data == null) || (data.length == 0))
//@			{
//@				throw new RuntimeException("IGP:: getImageBytes received null parameter or image lenght is 0");
//@			}
		//#endif

		int size = readInt(data);

		byte[] imageInBytes = new byte[size];
		System.arraycopy(data, dataOffset, imageInBytes, 0, size);

		dataOffset += size;

		return imageInBytes;
	}

	// Returns an Image from a byte array including the image size
	private static Image readImage(byte[] data)
	{
		//#ifdef DEBUG
//@			if ((data == null) || (data.length == 0))
//@			{
//@				throw new RuntimeException("IGP::readImage received null as parameter");
//@			}
		//#endif

		byte[] imageInBytes = getImageBytes(data);

		return Image.createImage(imageInBytes, 0, imageInBytes.length);
	}

	private static Image CreateImage(byte[] data, int offset, int size)
	{
		if (IGPConfig.createImageOffsetBug)
		{
			byte[] tempData = new byte[size];
			System.arraycopy(data, offset, tempData, 0, size);
			data = tempData;
			offset = 0;
		}

		return Image.createImage(data, offset, size);
	}

	private static String getString(int strIdx)
	{
		//return ("" + _StrMgr_Pack[strIdx]);
		return ("Test");
	}

#ifndef USE_NEW_RESOURCE_IGP
	private static int getFontMetric(int character, int metricFlag)
	{
		if (fontCoordBytesFixedOffset != 0)
		{
			if (metricFlag == FONT_X || metricFlag == FONT_Y)
			{
				int firstByte = fontDescriptor[(character * 6) + metricFlag] & 0xFF;
				int secondByte = fontDescriptor[(character * 6) + metricFlag + 1] & 0xFF;
				int value = 0;

				value |= ((secondByte & 0xFF) << 8);
				value |= (firstByte & 0xFF);
				return value;
			}
			else
			{
				int value = fontDescriptor[(character * 6) + metricFlag] & 0xFF;
				return value;
			}
		}
		else
		{
			return fontDescriptor[(character << 2) + metricFlag] & 0xFF;
		}
	}
#endif


	private static void drawString(int id, Graphics g, int x, int y, int anchor)
	{
		drawString(getString(id), g, IGP_SCRW, x, y, anchor);
	}

	private static void drawString(String str, Graphics g, int max_width, int x, int y, int anchor)
	{
		drawString(str, g, max_width, x, y, anchor, MAX_NB_LINES);
	}

	private static void drawString(String str, Graphics g, int max_width, int x, int y, int anchor, int maxLineNumber)
	{
	
	#ifdef USE_FONT_SPRITE //xuan
		if (bDetermineSizeOnly)
		{
			bDetermineSizeOnly = false;
			return;
		}
		if(_fontSprite != null && g != null)
        {
			str = str.replace('\n',' ');
			if (max_width == 0) max_width = IGP_SCRW - 10;//xuan			
			short[] t = _fontSprite.WraptextB(str, max_width, 0 , false);
			_fontSprite.SetCurrentPalette(0);
            _fontSprite.DrawPageB(g, str, t, x, y, 0, maxLineNumber, anchor, -1);
			// _fontSprite.DrawString(g, str, x, y, anchor);
        }
	#else //!USE_FONT_SPRITE
		String split = str;
		char [] splitB = new char [split.length()];		
		splitB = split.toCharArray();
		s_line_w[0] = 0;
		s_text_w = 0;

		int line = 0;
		int c;
		int i;
		boolean newLine = true;
		boolean from_newline = false;
		int len = split.length();
		int interLineExtend = 0; 

		// For specific small resolution add some space to inter line
		if (IGP_SCRW <= 176 && pagesType[currentPage] == IGP_TYPE_PROMOS)
		{
			interLineExtend += 2;
		}

		for (i = 0; i < len; i++)
		{
			c = splitB[i];

			boolean isBelowLineLimit = (line < MAX_NB_LINES);
			boolean hasLineBreak = (c == lineSeparator);
			boolean hasSlash = (c == '\\');
			boolean hasUpperN = ((i == len - 1) ? false : (splitB[i + 1] == 'N'));
			boolean hasLowerN = ((i == len - 1) ? false : (splitB[i + 1] == 'n'));

			if (isBelowLineLimit && (hasLineBreak || (hasSlash && (hasUpperN || hasLowerN))))
			{
			    // Fake '\n' char was found, skip 'n' or 'N' char.
			    if (hasSlash && (hasUpperN || hasLowerN))
			    {
			        i += 1;
			    }
				s_line_w[line] -= DEFAULT_CHAR_SPACING;                                
				if (s_line_w[line] > s_text_w)
				{
					s_text_w = s_line_w[line];
				}
				s_line_w[++line] = 0;
			}
			else if ((c != 0) && (c != 1))
			{
				s_line_w[line] += (getFontMetric(c, FONT_W) + DEFAULT_CHAR_SPACING);
			}
		}

		s_line_w[line] -= DEFAULT_CHAR_SPACING;

		if (s_line_w[line] > s_text_w)
		{
			s_text_w = s_line_w[line];
		}

		s_text_h = ((line + 1) * fontHeight) + (line * (DEFAULT_LINE_SPACING+interLineExtend));



		if (!bDetermineSizeOnly)
		{
			y += ( ((DEFAULT_LINE_SPACING+interLineExtend) * line ) / 2);
			line = 0;

			if ((anchor & Graphics.BOTTOM) != 0)
			{
				y -= s_text_h;
			}else if ((anchor & Graphics.VCENTER) != 0)
			{
				y -= (s_text_h >> 1);
			}

			int posX = x;

			newLine = true;
			from_newline = false;
			SetClip(g, 0, 0, IGP_SCRW, IGP_SCRH);

			for (i = 0; i < len; i++)
			{
				c = splitB[i];

				if (newLine)
				{
					posX = x;

					if ((anchor & Graphics.RIGHT) != 0)
					{
						posX -= s_line_w[line];
					}else if ((anchor & Graphics.HCENTER) != 0)
					{
						posX -= (s_line_w[line] >> 1);
					}

					newLine = false;
				}

				boolean isBelowLineLimit = (line < MAX_NB_LINES);
	            boolean hasLineBreak = (c == lineSeparator);
	            boolean hasSlash = (c == '\\');
	            boolean hasUpperN = ((i == len - 1) ? false : (splitB[i + 1] == 'N'));
	            boolean hasLowerN = ((i == len - 1) ? false : (splitB[i + 1] == 'n'));

	            if (isBelowLineLimit && (hasLineBreak || (hasSlash && (hasUpperN || hasLowerN))))
	            {
	                // Fake '\n' char was found, skip 'n' or 'N' char.
	                if (hasSlash && (hasUpperN || hasLowerN))
	                {
	                    i += 1;
	                }
					y += (fontHeight + DEFAULT_LINE_SPACING+interLineExtend -1);
					line++;
					newLine = true;
					from_newline = true;
				}
				else
				{
					if (from_newline)
					{
						from_newline = false;
						int c2 = splitB[i-2];
						if (c2 == ' ')
						{
							posX -= (getFontMetric(c2, FONT_W) + DEFAULT_CHAR_SPACING)>>1;
						}
						if (c == ' ')
						{
							posX += (getFontMetric(c, FONT_W) + DEFAULT_CHAR_SPACING)>>1;
							continue;
						}
					}
					SetClip(g, posX, y, getFontMetric(c, FONT_W), getFontMetric(c, FONT_H));
					DrawRegion(g, fontImage[drawColor], getFontMetric(c, FONT_X), getFontMetric(c, FONT_Y), getFontMetric(c, FONT_W), getFontMetric(c, FONT_H), posX, y);
					posX += (getFontMetric(c, FONT_W) + DEFAULT_CHAR_SPACING);
				}
			}
			SetClip(g, 0, 0, IGP_SCRW, IGP_SCRH);
		}
		else
		{
			bDetermineSizeOnly = false;
		}
	#endif //!USE_FONT_SPRITE
	}

	/**
	 * Call this during the loading of your game (after the Gameloft logo is shown)
	 *
	 * @param midlet: the MIDlet instance of the Game
	 * @param game        : the Canvas instance, necessary for Command Action
	 * @param scrw        : the Width of the device screen
	 * @param scrh        : the Height of the device screen
	 **/
	//#ifdef BLACKBERRY
//@	public static void initialize(UiApplication midlet, FullScreen game, int scrw, int scrh)
    //#else
	public static void initialize(MIDlet midlet, Canvas game, int scrw, int scrh)
	//#endif
	{
		IGP.initialize(midlet, game, null, scrw, scrh, null);
    }

	/**
	 * Call this during the loading of your game (after the Gameloft logo is shown)
	 *
	 * @param midlet: the MIDlet instance of the Game
	 * @param game        : the Canvas instance, necessary for Command Action
	 * @param scrw        : the Width of the device screen
	 * @param scrh        : the Height of the device screen
	 * @param cmdListener : the game CommandListener to be restored after exiting IGP
	 **/
	//#ifdef BLACKBERRY
//@	public static void initialize(UiApplication midlet, FullScreen game, int scrw, int scrh, Object cmdListener)
	//#else	
	public static void initialize(MIDlet midlet,Canvas game, ASprite font, int scrw, int scrh, CommandListener cmdListener)
	//#endif
	{
		System.out.println("---------------------------IGP.initialize");
        log("initialize(midlet = " + midlet + ", game = " + game + ", screenWidth = " + scrw + ", screenHeight = " + scrh + ", cmdListener = " + cmdListener + ")");
//#ifdef DEBUG
//@        debugIGPConfig();
//#endif
        
		//#ifdef BLACKBERRY_MIDP
//@		if (!IGPConfig.useStandardPlatformRequest)
//@		{
//@			sb = ServiceBook.getSB();
//@			records = sb.findRecordsByCid("BrowserConfig");
//@		}
		//#endif

		//#ifdef BLACKBERRY
//@		sb = ServiceBook.getSB();
//@		records = sb.findRecordsByCid("BrowserConfig");
		//#endif
		
	#ifdef USE_FONT_SPRITE
		_fontSprite = font;
	#endif

		IGP_SCRW = scrw;
		IGP_SCRH = scrh;
		IGP_HALF_SCRW = (IGP_SCRW >> 1);
		IGP_HALF_SCRH = (IGP_SCRH >> 1);
        SOFTKEYS_AREA_TOP = IGP_SCRH;
        canDrawHeader = IGP_SCRH >= 160;
        
		// Screen Positions
		GAMELOFT_LOGO_Y = (IGP_SCRH * 2) / 100; // 2%
		PRESS5_TO_GETIT_Y = IGP_SCRH / 2; // Half-Screen 
		PRESS5_TO_VISIT_Y = (IGP_SCRH * 93) / 100; // 93%

		// when IGPConfig.softkeyXOffsetFromBorders is not valid reset it to default value
        if (IGPConfig.leftSoftkeyXOffsetFromBorders < 0 || IGPConfig.leftSoftkeyXOffsetFromBorders > IGP_SCRW )
		{
			IGP_LSK_X = 2;
		}
        if (IGPConfig.rightSoftkeyXOffsetFromBorders < 0 || IGPConfig.rightSoftkeyXOffsetFromBorders > IGP_SCRW )
		{
			IGP_RSK_X = 2;
		}

		// ensure a single-time initialization
		if (MidletInstance != null)
		{
            log("MIDlet instance was already initialized");
			return;
		}

		// ensure MIDlet and Canvas instances aren't null
		if(midlet == null)
		{
			log("MIDlet instance can't be null");
		}
		if(game == null)
		{
			log("Canvas instance can't be null");
			return;
		}
		//#ifndef BLACKBERRY
		if(cmdListener != null)
		{
			useCommandBar = true;
			// intance neede to set the IGP command Listener
			if(igpInstance == null) igpInstance = new IGP();
			GameCmdListener = cmdListener;
		}
		//#endif		

		MidletInstance = midlet;
		GameInstance = game;

		if(IGPConfig.useHardcodedLinks)
		{
			initJadTable();
		}

		readAndParseURLs();

		String dummyVar = signature; //To Save signature variable value after Obfuscation
		dummyVar += "";
		
		isZVIPAvailable = checkZVip();
        
        if(isZVIPAvailable && useOVIFeature)
        {
            isZVIPAvailable = false;
//#ifdef DEBUG
//@		    throw new RuntimeException("You cannot use both ZVip and OVI features.");
//#endif
        }
		System.out.println("-----------------------------isAvailable:"+isAvailable);
	}

	private static boolean checkURL(String url, int checkFlags)
	{
		if (url == null)
		{
			return (checkFlags & k_Check_NULL) == 0;
		}

		url = url.trim();

		return ((((checkFlags & k_Check_NULL) == 0) || (url.length() != 0)) && (((checkFlags & k_Check_DEL) == 0) || (url.toUpperCase().compareTo("DEL") != 0)) && (((checkFlags & k_Check_NO) == 0) || ((url.toUpperCase().compareTo("NO") != 0) && (url.toUpperCase().compareTo("0") != 0))));
	}

	private static String getRedirLink(String appProp, String code, String urlTemplate)
	{
		String url = "";

		try
		{
			if ((urlTemplate != null) && (appProp != null) && (code != null))
			{
				int startPos = appProp.indexOf(code + "=");
				urlTemplate = urlTemplate.trim();

				if ((startPos >= 0) && (urlTemplate.length() > 0))
				{
					startPos += (code.length() + 1);

					int endPos = appProp.indexOf(s_GAME_SEPARATOR, startPos);

					if (endPos < 0)
					{
						endPos = appProp.length();
					}

					url = appProp.substring(startPos, endPos);

					url = url.trim();

					if ((url.length() == 0) || (url.compareTo("0") == 0) || (url.toUpperCase().compareTo("NO") == 0))
					{
						url = "";
					}
					else if ((url.toUpperCase().compareTo("DEL") != 0) && (code.compareTo("OP") != 0))
					{
						int separatorPos = urlTemplate.indexOf(s_PLACEHOLDER);
						if (separatorPos >= 0)
						{
						    url = urlTemplate.substring(0, separatorPos) + url + urlTemplate.substring(separatorPos + s_PLACEHOLDER.length());
						}
						else
						{
						    url = urlTemplate;
						}
					}
				}
			}
		}
		//#ifdef BLACKBERRY		
//@		catch (Throwable e)
			//#else			
			catch (Exception e)
			//#endif
			{
				url = "";
			}

		return url;
	}

	private static void parseSplash(int page, String code, int check, String jadEntry, String urlTemplate)
	{
		try
		{
			String url = "";

			if (b_useRedir)
			{
//#ifdef BLACKBERRY
//@				if (s_jadNotFound)
//@				{
//@					url = getRedirLink(s_igpPromosFake, code, urlTemplate);
//@				}
//@				else
//@				{
//@					url = getRedirLink(getAppProperty(jadEntry), code, urlTemplate);
//@				}
//#else
				url = getRedirLink(getAppProperty(jadEntry), code, urlTemplate);
//#endif
			}
			else
			{
				url = getAppProperty(URL_PREFIX + "-" + code);
			}

			boolean validURL = checkURL(url, check);

			if (validURL)
			{
				if ((url.toUpperCase().compareTo("NO") != 0) || (url.toUpperCase().compareTo("0") != 0))
				{
					// The page is valid
					pagesValid[page] = true;

					// This page has this url
					pagesURLs[page] = url;

					// Add promo index tracking only for PROMOS
					if (pagesValid[page] && page != PAGE_PROMOTION)
					{
						validPromos ++;
						pagesType[page] = IGP_TYPE_PROMOS;
						if (b_useIGPRedir)
						{
						    pagesURLs[page] += s_CTG + s_PROMO_SCREEN_HEADER + (validPromos < 10 ? "0" : "") + validPromos;
					    }
					}
				}
			}
		}
//#ifdef BLACKBERRY		
//@		catch (Throwable e)
//#else			
		catch (Exception e)
//#endif
		{
//#ifdef DEBUG
//@			e.printStackTrace();
//#else
			e = null;
//#endif
		}
	}
	
    private static void parseList(int list, String[] list_codes, int page, int currentList_item_start, String jadEntry)
	{
		int itemNo = list_codes.length;

		LIST_URLs[list] = new String[itemNo];
		ValidLISTIdx[list] = new int[itemNo];
		LIST_Texts[list] = new int[itemNo];

		int nbItems = 0;

		if (optionalFeatures[OPTIONAL_FEATURE_ORANGE_GLOBAL])
		{
			return;
		}

		String urlTemplate = "";

		if (b_useRedir)
		{
			try
			{
				jadEntry = getAppProperty(jadEntry);

				if (list != COMPLETE_CATALOG_LIST)
				{
					urlTemplate = s_URL_TEMPLATE_GAME;
				}
				else if (s_urlOperator.length() > 0 && b_useIGPRedir)
				{
					urlTemplate = s_urlOperator + s_ctg_PLACEHOLDER;
				}
				else if (s_urlOperator.length() > 0)
				{
				    urlTemplate = s_urlOperator;
				}
			}
//#ifdef BLACKBERRY		
//@			catch (Throwable e)
//#else			
			catch (Exception e)
//#endif
			{
//#ifdef DEBUG
//@			    e.printStackTrace();
//#else
				e = null;
//#endif
			}
		}
        
		for (int j = 0; j < list_codes.length; j++)
		{
			try
			{
				String url = "";

				if ((list != COMPLETE_CATALOG_LIST) && (j == (itemNo - 1)))
				{
					if (!b_useRedir)
					{
						url = getAppProperty(PAGE_LIST_PREFIXES[list]);
					}
					else if (s_urlOperator.length() > 0)
					{
						String jadField = "";
//#ifdef BLACKBERRY
//@						try
//@						{
//@							jadField = getAppProperty(s_IGP_CATEGORIES).trim();
//@						}
//@						catch (Throwable e)
//@						{
//@							if (s_igpCategoriesFake == null)
//@							{
//@								for (int i = 0; i < CatalogList.length; i++)
//@								{
//@									s_igpCategoriesFake += CatalogList[i] + "=" + CatalogList[i] + ";";
//@								}
//@								jadField = "";
//@							}
//@							jadField = s_igpCategoriesFake;
//@						}
//#else
						jadField = getAppProperty(s_IGP_CATEGORIES);
//#endif
						url = getRedirLink(jadField, list_codes[j], s_urlOperator + (b_useIGPRedir ? s_ctg_PLACEHOLDER : ""));
					}
				}
				else if (b_useRedir)
				{
					// complete catalogue
					if (list_codes[j].compareTo(IGP_GLDT) == 0)
					{
						url = getRedirLink(jadEntry, list_codes[j], s_URL_TEMPLATE_GAME);
					}
					else if (list_codes[j].compareTo(IGP_CATALOG) == 0)
					{
						url = s_urlOperator;
					}
					else
					{
						url = getRedirLink(jadEntry, list_codes[j], urlTemplate);
					}
				}
				else
				{
					url = getAppProperty(PAGE_LIST_PREFIXES[list] + "-" + list_codes[j]);
				}

				if (checkURL(url, k_Check_ALL))
				{
					LIST_URLs[list][nbItems] = url;
					ValidLISTIdx[list][nbItems++] = j;
                    int stringIndex = currentList_item_start - (list_codes.length - 1) + j;
					LIST_Texts[list][j] = stringIndex;
				}
			}
//#ifdef BLACKBERRY		
//@			catch (Throwable e)
//#else			
			catch (Exception e)
//#endif
			{
//#ifdef DEBUG
//@			    e.printStackTrace();
//#else
				e = null;
//#endif
			}
		}

		if (nbItems > 0)
		{
			pagesValid[PAGE_WN + list] = true; // list = 0 WN | list = 1 BS 
			LIST_nbItems[list] = nbItems;
		}
	}

	private static String[] readHeaderList(byte[] data)
	{
		String[] list = new String[readInt(data)];

		for (int i = 0; i < list.length; i++)
		{
			int strLen = readInt(data);
			list[i] = new String(data, dataOffset, strLen); //, "UTF-8");
			dataOffset += strLen;
		}
		return list;
	}
    
	private static void initGenericIndex(String[] PROMOS)
	{
		// Init some generic text index
		int firstIndex = (((PROMOS.length - 1) > 0) ? (PROMOS.length - 1) : (0)) + 4; //Generic Text : SPECIAL OFFERS!, WHAT'S NEW, BESTSELLERS, COMPLETE CATALOGUE
		firstIndex += 1;
		IGP_TEXT_CATALOG_ORANGE = firstIndex;
		IGP_TEXT_VISIT_YOUR_PORTAL = IGP_TEXT_CATALOG_ORANGE + 1;
		IGP_TEXT_PRESS_5_TO_VISIT = IGP_TEXT_VISIT_YOUR_PORTAL + 1;
		IGP_TEXT_PRESS_5_TO_GET_IT = IGP_TEXT_PRESS_5_TO_VISIT + 1;
		IGP_TEXT_PRESS_OK_TO_VISIT = IGP_TEXT_PRESS_5_TO_GET_IT + 1;
		IGP_TEXT_PRESS_OK_TO_GET_IT = IGP_TEXT_PRESS_OK_TO_VISIT + 1;        
		IGP_TEXT_SELECT = IGP_TEXT_PRESS_OK_TO_GET_IT + 1;
		IGP_TEXT_BACK = IGP_TEXT_SELECT + 1;
		IGP_TEXT_TOUCHVISIT = IGP_TEXT_BACK + 1;
		IGP_TEXT_TOUCHGET = IGP_TEXT_TOUCHVISIT + 1;
		IGP_TEXT_DPADVISIT = IGP_TEXT_TOUCHGET + 1;
		IGP_TEXT_DPADGET = IGP_TEXT_DPADVISIT + 1;
//#ifdef ENABLE_GLIVE		
//@		IGP_TEXT_GLIVE_1 = IGP_TEXT_DPADGET + 1;
//@		IGP_TEXT_GLIVE_2 = IGP_TEXT_GLIVE_1 + 1;
//@		IGP_TEXT_GLIVE_FREECHAT = IGP_TEXT_GLIVE_2 + 1;
//@		IGP_EXIT_CONFIRM = IGP_TEXT_GLIVE_FREECHAT + 1;
//@		IGP_TEXT_WN_GAME_0 = IGP_EXIT_CONFIRM + 1;
//@		IGP_TEXT_BS_GAME_0 = IGP_TEXT_WN_GAME_0 + WNList.length;
//@		IGP_TEXT_COMPLETE_CATALOGUE = IGP_TEXT_BS_GAME_0 + BSList.length;
//#ifdef ENABLE_VODA		
//@		IGP_TEXT_5_VODAFONE = IGP_TEXT_COMPLETE_CATALOGUE + 1;
//@		IGP_TEXT_TOUCH_VODAFONE = IGP_TEXT_5_VODAFONE + 1;
//#endif		
//#else	
		IGP_EXIT_CONFIRM = IGP_TEXT_DPADGET + 1;
        int lastValidIndex = IGP_EXIT_CONFIRM;
        
        // WN Titles & More WN
        if (WNList.length > 1)
        {
            IGP_TEXT_WN_GAME_0 = lastValidIndex + 1;
            IGP_TEXT_MORE_WN = IGP_TEXT_WN_GAME_0 + (WNList.length - 1);
        }
        else
        {
            IGP_TEXT_MORE_WN = lastValidIndex + 1;
        }
        lastValidIndex = IGP_TEXT_MORE_WN;
        
        // BS Titles & More BS
        if (BSList.length > 1)
        {
            IGP_TEXT_BS_GAME_0 = lastValidIndex + 1;
            IGP_TEXT_MORE_BS = IGP_TEXT_BS_GAME_0 + (BSList.length - 1);
        }
        else
        {
            IGP_TEXT_MORE_BS = lastValidIndex + 1;
        }
        lastValidIndex = IGP_TEXT_MORE_BS;
        
        IGP_TEXT_COMPLETE_CATALOGUE = lastValidIndex + 1;
        lastValidIndex = IGP_TEXT_COMPLETE_CATALOGUE;
//#ifdef ENABLE_VODA
//@		IGP_TEXT_5_VODAFONE = lastValidIndex + 1;
//@		IGP_TEXT_TOUCH_VODAFONE = IGP_TEXT_5_VODAFONE + 1;
//@     lastValidIndex = IGP_TEXT_TOUCH_VODAFONE;
//#endif
//#endif
        IGP_TEXT_LOADING = lastValidIndex + 1;
	}

	private static void readAndParseURLs()
	{
		// page arrays initialization
		String[] PROMOS;	

		try
		{
		#ifdef USE_NEW_RESOURCE_IGP
			AvailableLanguages = _AvailableLanguages;
			PROMOS = __PROMOS;
			WNList = __WNList;
			BSList = __BSList;
			CatalogList = __CatalogList;
			optionalFeatures = __optionalFeatures;
		#else
			boolean loaded = openDataIGP();
			if (!loaded)
			{
				log("dataIGP file can't be loaded or found.");
				isAvailable = false;
				return;
			}

			byte[] data = readDataIGP(0);

			//Read Header length
			readInt(data);

			// Read Game IGP-CODE
			int strLen = readInt(data);
			GameName = new String(data, dataOffset, strLen);
			dataOffset += strLen;

			// Read Operator code.
			strLen = readInt(data);
			operatorCode = new String(data, dataOffset, strLen);
			dataOffset += strLen;

			// Read screen width.
			strLen = readInt(data);
			screenWidth = new String(data, dataOffset, strLen);
			dataOffset += strLen;

			// Read screen height.
			strLen = readInt(data);
			screenHeight = new String(data, dataOffset, strLen);
			dataOffset += strLen;
			
			// Read generics size
			strLen = readInt(data);
			genericsSize = new String(data, dataOffset, strLen);
			dataOffset += strLen;

			// Read "UseLowDetail"
			useLowDetail = (readInt(data) == 1);

			// Read "UseLowDetailSplashes"
			useLowDetailSplashes = (readInt(data) == 1);

            // Read "UseTransformSpriteRegion"
            useTransformSpriteRegion = (readInt(data) == 1);

			// Read number of languages            
			AvailableLanguages = readHeaderList(data);

			PROMOS = readHeaderList(data);
			WNList = readHeaderList(data);
			BSList = readHeaderList(data);
			CatalogList = readHeaderList(data);
//#ifdef DEBUG
//@            log("dataIGP values: ");
//@            currentDebugSpacing = IGP_DEBUG_TAB_SPACING;
//@            log("Game Code: " + GameName);
//@            log("Operator Code: " + operatorCode);
//@            log("Screen width: " + screenWidth);
//@            log("Screen height: " + screenHeight);
//@            log("Generics size: " + genericsSize);
//@            log("UseLowDetail: " + useLowDetail);
//@            log("UseLowDetailSplashes: " + useLowDetailSplashes);
//@            for (int i = 0; i < AvailableLanguages.length; i++)
//@                log("Language " + i +  ": " + AvailableLanguages[i]);
//@            for (int i = 0; i < PROMOS.length; i++)
//@                log("Promo " + i + ": " + PROMOS[i]);
//@            for (int i = 0; i < WNList.length; i++)
//@                log("WN " + i + ": " + WNList[i]);
//@            for (int i = 0; i < BSList.length; i++)
//@                log("BS " + i + ": " + BSList[i]);
//@            for (int i = 0; i < CatalogList.length; i++)
//@                log("Operator Feature " + i + ": " + CatalogList[i]);
//@            currentDebugSpacing = 0;
//#endif
			for (int i = 0; i < optionalFeatures.length; i++)
			{
				optionalFeatures[i] = readInt(data) == 1;
			}

			try
			{
				int len = readInt(data);
				DATA_VERSION = new String(data, dataOffset, len); // dataOffset + 2(int size)
				log("IGP dataIGP version: " + DATA_VERSION);

				if (DATA_VERSION.equals("2.2z"))
				{
					fontCoordBytesFixedOffset = 2;
					x_ArrowNumberLeft = 6*2;
					x_ArrowNumberRight = 3*2;
					FONT_Y = 2;
					FONT_W = 4;
					FONT_H = 5;
				}

				if (!DATA_VERSION.startsWith(CODE_VERSION))
				{
					log("Invalid dataIGP file, dataIGP file IGP Version : " + DATA_VERSION);
					log("IGP Class version : " + CODE_VERSION);
//#ifdef DEBUG                    
//@					throw new RuntimeException("Invalid dataIGP file version: needed(" + CODE_VERSION + "), found(" + DATA_VERSION + ")");
//#endif                    
				}
			}
//#ifdef BLACKBERRY		
//@			catch (Throwable e)
//#else			
			catch (Exception e)
//#endif
			{
//#ifdef DEBUG
//@			    e.printStackTrace();
//#else
				e = null;
//#endif
                isAvailable = false;
			}
			closeDataIGP();
		#endif //USE_NEW_RESOURCE_IGP
		}
//#ifdef BLACKBERRY		
//@		catch (Throwable e)
//#else			
        catch (Exception e)
//#endif
        {
//#ifdef DEBUG
//@            log("Invalid dataIGP file header. Maybe the dataIGP file is old");
//@            e.printStackTrace();
//#endif
            isAvailable = false;
			return;
        }

		initGenericIndex(PROMOS); 
		NumberOfPromos = PROMOS.length;
		promosSplashesOverriden = new Image[NumberOfPromos];
        
		numberOfPages = NumberOfPromos
			+ 1 // chunkPromotion
			+ 1 // chunkWN
			+ 1 // chunkBS
			+ 1 // chunkCatalog
//#ifdef ENABLE_GLIVE
//@			+ (optionalFeatures[OPTIONAL_FEATURE_GLLIVE] ? 1 : 0) // chunkGLive
//#endif							
			+ (optionalFeatures[OPTIONAL_FEATURE_ORANGE_GLOBAL] ? 1 : 0) // chunkFROG
//#ifdef ENABLE_VODA									
//@			+ (optionalFeatures[OPTIONAL_FEATURE_VODAFONE] ? 1 : 0); // chunkVodafone
//#else
		;		
//#endif		

		// Set Optional Features chunk indexes
		for (int i = 0; i < optionalFeatures.length; i++)
		{
			if (optionalFeatures[i])
			{
				IGP_TYPE_OPTIONAL_FEATURES[i] = ++ lastLoadStep;
			}
			else
			{
				IGP_TYPE_OPTIONAL_FEATURES[i] = -- firstLoadStep;
			}
		}

		// Set IGP_TYPE_END index ( 1 plus the lastLoadStep )
		IGP_TYPE_END = ++ lastLoadStep;
        
		pagesURLs  = new String[numberOfPages];
		pagesValid = new boolean[numberOfPages];
        pagesType  = new int[numberOfPages];
        
		for (int i = 0; i < pagesValid.length; i++)
		{
			pagesValid[i] = false;
		}
        
        // Page indexes
        PAGE_PROMOTION = NumberOfPromos;
        PAGE_WN = PAGE_PROMOTION + 1;
        PAGE_BS = PAGE_WN + 1;
        PAGE_OPERATOR = PAGE_BS + 1;
//#ifdef ENABLE_GLIVE
//@        PAGE_GLIVE = PAGE_OPERATOR + 1;
//@        PAGE_ORANGE = PAGE_GLIVE + 1;
//#else
        PAGE_ORANGE = PAGE_OPERATOR + 1;
//#endif
		LIST_URLs = new String[NUMBER_OF_LIST][];
		ValidLISTIdx = new int[NUMBER_OF_LIST][];
		LIST_Texts = new int[NUMBER_OF_LIST][];
		LIST_nbItems = new int[NUMBER_OF_LIST];
		
		

		s_URL_TEMPLATE_OVI = getAppProperty(k_URL_OVI);
		s_POWER_TEMPLATE_OVI = getAppProperty(k_POWER_OVI);
		
		// Check if we have OVI URL on the JAD file.
		if (s_URL_TEMPLATE_OVI != null && s_POWER_TEMPLATE_OVI.toLowerCase().equals(s_POWER_DEFAULT_VALUE.toLowerCase()))
		{
		    urlOVI = s_URL_TEMPLATE_OVI;
		    useOVIFeature = true;
		    // OVI feature will void standard IGP.
		    isAvailable = true;
            return;
		}

		//#ifdef ENABLE_VODA
//@		// Vodafone Demo
//@		String vodafoneField = getAppProperty(IGP_PREFIX + "-" + IGP_VODAFONE);
//@		if (vodafoneField != null)
//@		{
//@			int vodafoneValue = Integer.parseInt(vodafoneField);            
//@			if (optionalFeatures[OPTIONAL_FEATURE_VODAFONE] && vodafoneValue == 1)
//@			{
//@				bVodafoneCustomization = optionalFeatures[OPTIONAL_FEATURE_VODAFONE] && (vodafoneValue == 1);
//@			}
//@		}
		//#endif		

		// detect if we have redir or direct links
		try
		{                        
			s_URL_TEMPLATE_GAME = getAppProperty(k_URL_TEMPLATE_GAME);
			if (s_URL_TEMPLATE_GAME != null)
			{
				s_URL_TEMPLATE_GAME = s_URL_TEMPLATE_GAME.trim();			
				b_useRedir = true;
				if (s_URL_TEMPLATE_GAME.indexOf(s_IGP_REDIR_URL) != -1)
				{
					b_useIGPRedir = true;
				}
			}
		}
//#ifdef BLACKBERRY
//@		catch (Throwable e)
//@		{
//@			s_jadNotFound = true;
//@			if (s_jadNotFound)
//@			{
//@			    b_useRedir = true;
//@				b_useIGPRedir = true;
//@				s_URL_TEMPLATE_GAME = "http://ingameads.gameloft.com/redir/?from="+GameName+"&op=RIMA&game=XXXX&ver=2.1";
//@				s_igpPromosFake = "";
//@
//@				// parse promos
//@				for (int promo = 0; promo < NumberOfPromos; promo++)
//@				{               	
//@					s_igpPromosFake += PROMOS[promo] + "=" + PROMOS[promo] + ";";
//@				}
//@			}
//#ifdef DEBUG
//@			e.printStackTrace();
//#else
//@			e = null;
//#endif
//@		}
//@		if (s_jadNotFound)
//@		{
//@		    b_useRedir = true;
//@			b_useIGPRedir = true;
//@			s_URL_TEMPLATE_GAME = "http://ingameads.gameloft.com/redir/?from="+GameName+"&op=RIMA&game=XXXX&ver=2.1";
//@			s_igpPromosFake = "";
//@
//@			// parse promos
//@			for (int promo = 0; promo < NumberOfPromos; promo++)
//@			{               	
//@				s_igpPromosFake += PROMOS[promo] + "=" + PROMOS[promo] + ";";
//@			}
//@		}
//#else
		catch (Exception e)
		{}
//#endif

		// parse promos
		for (int promo = 0; promo < NumberOfPromos; promo++)
		{               	
			parseSplash(promo, PROMOS[promo], k_Check_PROMO, s_IGP_PROMOS, s_URL_TEMPLATE_GAME);
		}

		String l_urlOperator = null;
		try
		{
			l_urlOperator = getAppProperty(URL_OPERATOR).trim();

			if (checkURL(l_urlOperator, k_Check_ALL))
			{
				s_urlOperator = l_urlOperator;
			}

			// Promotion Text
			s_textPt = getAppProperty(IGP_PT);
		}
//#ifdef BLACKBERRY		
//@        catch (Throwable e)
//@        {
//@        	b_useRedir = true;
//@			b_useIGPRedir = true;
//@			
//@			s_urlOperator = "http://" + s_IGP_REDIR_URL + "/?from=" + GameName + "&op=RIMA&ver=2.1";
//@			
//@			log("s_urlOperator: " + s_urlOperator);
//@			
//#else			
        catch (Exception e)
        {
//#endif
//#ifdef DEBUG
//@            e.printStackTrace();
//#else
            e = null;
//#endif
        }

		if (!optionalFeatures[OPTIONAL_FEATURE_ORANGE_GLOBAL])
		{
			// Parse PROMOTION
			if (b_useRedir)
			{
				if (checkURL(s_urlOperator, k_Check_ALL))
				{
					parseSplash(NumberOfPromos, IGP_PROMOTION, k_Check_ALL, s_IGP_CATEGORIES, s_urlOperator +(b_useIGPRedir?s_ctg_PLACEHOLDER:""));
				}
			}
			else
			{
				String promotionURL = getAppProperty(URL_PREFIX + "-" + IGP_PROMOTION);
				if (promotionURL != null)
				{
					promotionURL.trim();
					if (checkURL(getAppProperty(URL_PREFIX + "-" + IGP_PROMOTION), k_Check_ALL))
					{
						pagesURLs[PAGE_PROMOTION] = promotionURL;
						pagesValid[PAGE_PROMOTION] = true;
                        pagesType[PAGE_PROMOTION] = IGP_TYPE_PROMOTION;
					}
				}
			}
		}

		// Parse WN and BS
//#ifdef BLACKBERRY
//@		if (!s_jadNotFound)
//#endif
		{
			parseList(WN_LIST, WNList, NumberOfPromos + 1, IGP_TEXT_MORE_WN, s_IGP_WN);
			parseList(BS_LIST, BSList, NumberOfPromos + 2, IGP_TEXT_MORE_BS, s_IGP_BS);
        }

		if(!(optionalFeatures[OPTIONAL_FEATURE_ORANGE_GLOBAL]))
		{
			// Parse OPERATOR
			if (b_useRedir)
			{
				String jadField = "";
//#ifdef BLACKBERRY
//@				try
//@				{
//@					jadField = getAppProperty(s_IGP_CATEGORIES).trim();
//@				}
//@				catch(Throwable e)
//@				{
//@					jadField = "";
//@					
//@					b_useRedir = true;
//@					b_useIGPRedir = true;
//@					
//@					if (s_igpCategoriesFake == null)
//@					{
//@						for (int i = 0; i < CatalogList.length; i++)
//@						{
//@							s_igpCategoriesFake += CatalogList[i] + "=" + CatalogList[i] + ";";
//@						}
//@					}
//@					jadField = s_igpCategoriesFake;
//@				}
//#else
				jadField = getAppProperty(s_IGP_CATEGORIES);
//#endif				
				if (checkURL(getRedirLink(jadField, "OP", s_urlOperator), k_Check_ALL))
				{
					if (checkURL(s_urlOperator, k_Check_ALL))
					{
						pagesValid[PAGE_OPERATOR] = true;
					}
					pagesURLs[PAGE_OPERATOR] = s_urlOperator;
				}
			}
			else
			{
				if (checkURL(s_urlOperator, k_Check_ALL))
				{
					pagesURLs[PAGE_OPERATOR] = s_urlOperator;
					pagesValid[PAGE_OPERATOR] = true;
				}
			}
			if (pagesValid[PAGE_OPERATOR] && b_useIGPRedir)
			{
				pagesURLs[PAGE_OPERATOR] += s_CTG + s_OPERATOR;
			}
		}

		if (optionalFeatures[OPTIONAL_FEATURE_ORANGE_GLOBAL])
		{
			try
			{
				if (checkURL(s_urlOperator, k_Check_ALL))
				{                            
					pagesURLs[PAGE_ORANGE] = s_urlOperator;
					pagesValid[PAGE_ORANGE] = true;
				}
			}
			//#ifdef BLACKBERRY		
//@			catch (Throwable e)
				//#else			
				catch (Exception e)
				//#endif
				{
					//#ifdef DEBUG
//@					e.printStackTrace();
					//#else
					e = null;
					//#endif

				}
		}        

//#ifdef ENABLE_GLIVE
//@		// GLive
//@		pagesURLs[PAGE_GLIVE] = null;
//@		pagesValid[PAGE_GLIVE] = false;
//@
//@		if (!optionalFeatures[OPTIONAL_FEATURE_ORANGE_GLOBAL])
//@		{
//@			try
//@			{
//@				GLiveURL = null;
//@
//@				if(!b_useRedir)
//@				{
//@					GLiveURL = getAppProperty(s_GLive).trim();
//@				}
//@				else if (checkURL(s_urlOperator, k_Check_ALL))
//@				{
//@					GLiveURL = getRedirLink(getAppProperty(s_IGP_CATEGORIES), "GLIVE", s_urlOperator+(b_useIGPRedir?s_ctg_PLACEHOLDER:""));
//@				}			
//@				if(checkURL( GLiveURL , k_Check_ALL) && optionalFeatures[OPTIONAL_FEATURE_GLLIVE])
//@				{
//@					PageURLs[PAGE_GLIVE] = GLiveURL;
//@					PageValid[PAGE_GLIVE] = true;
//@
//@				}            
//@			}
//#ifdef BLACKBERRY
//@            catch (Throwable e)
//#else
//@            catch (Exception e)
//#endif
//@            {
//#ifdef DEBUG
//@                e.printStackTrace();
//#else
//@                e = null;
//#endif
//@			}
//@		}
//#endif
		ValidRealPages = getPagesCount();
		if (ValidRealPages > 0)
		{
            isAvailable = true;
		}
        else
        {
            log("There are no valid pages");
        }
		log("isAvailable = " + isAvailable);
	}

	/**
	 * Determines if the IGP is available or not
	 * @return true if the the IGP is available, false otherwise.
	 */
	public static boolean IsAvailable()
	{
	    return isAvailable;
	}

	/**
	 * Determines if the IGP is available or not
	 * @return true if the the IGP is available, false otherwise.
	 */
	public static boolean isAvailable()
	{
	    return isAvailable;
	}

	/** Must be called AFTER initialize(), and BEFORE entering into the IGP state
	 *
	 * @param loadingMsg :        The String Message when loading IGP
	 * @param appLanguage:        the language index to load()
	 * @param _imgBBGame
	 */
	public static void enterIGP(String loadingMsg, int appLanguage)
	{
	    log("enterIGP(loadingMsg = " + loadingMsg + ", appLanguage = " + appLanguage + " (" + AvailableLanguages[appLanguage] + ")");
		
		// Initialize backbuffer image.
		if (IGPConfig.screenRotation != IGPConfig.SCREEN_ROTATION_NONE)
		{
		    imgBackBuffer = Image.createImage(IGP_SCRW, IGP_SCRH);
		}

	    if (useOVIFeature)
        {
	        log("Using OVI feature, standard IGP is deactivated!");
	        // Determine platform request type.
	        if (IGPConfig.platformRequestType == PLATFORM_REQUEST_ON_NEW_THREAD)
	        {
	            initializePlatformRequestServer();
                
	            // Start server.
	            IGPRunning = true;
	            IGPServerURL = urlOVI;
	        }
	        else
	        {   
	            URLPlatformRequest = urlOVI;
	            doPlatformRequest();
	        }
        }
        else
        {
            // Standard IGP.
            if (IGPConfig.isTouchScreen)
            {
                resetTouchVariables();
            }
            enterZVIP = false;
            commonConstruct(loadingMsg, appLanguage);
            // Loading steps
            if (IGPConfig.loadType == LOAD_RESOURCES_ON_START)
            {
                TotalLoadingSteps = NB_LOAD_STEPS_GENERIC + numberOfPages;
            }
            else
            {
                TotalLoadingSteps = NB_LOAD_STEPS_GENERIC;
            }
            currentPage = getFirstValidPage();
            //#ifndef BLACKBERRY
            if(useCommandBar)
            {
                GameInstance.setCommandListener(igpInstance);
			}
                //#endif
        }
        MarkIGPsAsVisited();
     }

    /** Must be called to entering into the Vip Zone
     *	This is not a IGP state, should not use update(...)
     */
    public static void enterZVIP()
    {
		enterZVIP = true;
        if (!isZVipAvailable())
        {
//#ifdef DEBUG
//@         log("Error: ZVIP isn't available!");
//#endif
            return;
        }
		currentState = STATE_ZVIP;

        if (IGPConfig.platformRequestType == PLATFORM_REQUEST_ON_NEW_THREAD)
		{
            initializePlatformRequestServer();

			// create the server
			IGPRunning = true;
            IGPServerURL = urlZVIP;
		}
		else
		{
			URLPlatformRequest = urlZVIP;
	        //doPlatformRequest();
		}
	}

    public static boolean isZVipAvailable()
	{   
		return isZVIPAvailable;
	}
    
	private static boolean checkZVip()
	{
		//Zona Vip is available only for spain
		boolean haveSpanish = false;
		for (int i = 0; i < AvailableLanguages.length; i++)
		{
			if(AvailableLanguages[i].equals("SP"))
			{
				haveSpanish = true;
			}
		}    
		if(!haveSpanish)
		{
			return false;
		}

		try
		{
			String categories = getAppProperty(s_IGP_CATEGORIES);
			if( categories != null && categories.indexOf(IGP_ZVIP) != -1 )
			{
				int start = categories.indexOf(IGP_ZVIP)+IGP_ZVIP.length()+1;
				int end   = start + IGP_ZVIP.length();
				if(end >= categories.length())
				{
					return false;
				}
				if(!categories.substring(start,end).equals(IGP_ZVIP))
				{
					return false;
				}

				urlZVIP = s_urlOperator;

				if(b_useIGPRedir)
				{
					urlZVIP += s_ctg_PLACEHOLDER;
					int separatorPos = urlZVIP.indexOf(s_PLACEHOLDER);
					if (separatorPos >= 0)
					{
						urlZVIP = urlZVIP.substring(0, separatorPos) + IGP_ZVIP + urlZVIP.substring(separatorPos + s_PLACEHOLDER.length());
					}
					if (urlZVIP.length() == 0)
					{
						return false;
					}
				}
			}
			else
			{          
				// Direct Link
				urlZVIP = getAppProperty(URL_PREFIX + "-" + IGP_ZVIP);
				if (urlZVIP == null)
				{
					return false;
				}
				return true;
			}
		}
		catch(Exception e)
		{
			//#ifdef DEBUG
//@			e.printStackTrace();
			//#endif 
			return false;
		}
		return true;
	}



		private static void commonConstruct(String loadingMsg, int appLanguage)
		{
			if ((appLanguage < 0) || (appLanguage >= AvailableLanguages.length))
			{
				//#ifdef DEBUG
//@				String error = "IGP::Invalid language index ";
//@				error += ((AvailableLanguages.length == 0) ? "No Languages Availables" : (("Available languages index: ") + ((AvailableLanguages.length == 1) ? (" 0") : (" From 0 to " + (AvailableLanguages.length - 1)))));
//@				throw new RuntimeException(error);
				//#else
				return;
				//#endif
			}

			CurrentLanguage = (appLanguage <= AvailableLanguages.length) ? appLanguage : 0;
			StringLoading = loadingMsg;
			//BOX_COLOR = 0xFF0000;

			CurrentLoadingStep = -1;
			currentState = STATE_LOADING;
			lastState = -1;

			currentPage = 0;
			s_igpListDisplayItemStart = 0;
			s_ItemListIndex = 0;

			s_line_w = new int[MAX_NB_LINES];

			IGPRunning = true;

			//#ifdef BLACKBERRY
//@			sysFont = Font.getDefault().derive(Font.PLAIN, 10);
			//#else		
			sysFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
			//#endif		

			// Reset softkeys labels
			s_displaySoftKeysIcons = 0;
			s_displaySoftKeysBackgrounds = 0;

            // Start platform request server.
            if (IGPConfig.platformRequestType == PLATFORM_REQUEST_ON_NEW_THREAD)
            {
                initializePlatformRequestServer();
            }
		}

        // Starts the platform request server.
        private static void initializePlatformRequestServer()
        {
            log("initializePlatformRequestServer()");

            // Ensure to have an instance from IGP.
            if (igpInstance == null)
            {
                igpInstance = new IGP();
            }

			// create the server
			new Thread(igpInstance).start();
        }

		private static int getPagesCount()
		{
			int nbValidPages = 0;

			for (int i = 0; i < pagesValid.length; i++)
			{
				if (pagesValid[i])
				{
					nbValidPages++;
				}
			}
			return nbValidPages;
		}

		// Reuturn the chunk index to load according to the specified page
		private static int getPageChunkIndex(int currentPage)
		{
			if (pagesType[currentPage] == IGP_TYPE_PROMOS)
			{
				return pagesType[currentPage];
			}
			else if (currentPage == PAGE_PROMOTION)
			{
				return IGP_TYPE_PROMOTION;
			}
			else if (currentPage == PAGE_WN)
			{
				return IGP_TYPE_WN;
			}
			else if (currentPage == PAGE_BS)
			{
				return IGP_TYPE_BS;
			}
			else if (currentPage == PAGE_OPERATOR)
			{
				return IGP_TYPE_CATALOG;
			}
			else if (currentPage == PAGE_ORANGE)
			{
				return IGP_TYPE_OPTIONAL_FEATURES[OPTIONAL_FEATURE_ORANGE_GLOBAL];
			}
			//#ifdef ENABLE_GLIVE         
//@			else if (currentPage == PAGE_GLIVE)
//@			{
//@				return IGP_TYPE_OPTIONAL_FEATURES[OPTIONAL_FEATURE_GLLIVE];
//@			}
			//#endif         
			else
			{
				//#ifdef DEBUG
//@				log("Error at getPageChunkIndex(), no return for: " + currentPage);
				//#endif            
				return -1;
			}
		}

		private static int getFirstValidPage()
		{
			int currentPage = 0;
			while (currentPage < pagesValid.length)
			{
				if (pagesValid[currentPage])
				{
					return currentPage;
				}
				currentPage++;
			}
			return -1;
		}

		private static void loadPromotionText() 
		{
			if (s_textPt == null)
			{            
				return;
			}

			int textLenght = s_textPt.length();
			if (!(textLenght > 0))
			{
				s_textPt = null;
				return;
			}

			boolean newLineFound = false;
			int lastSpace = 0;
			int occupiedTotal = 0;
			int occupiedUntilLastSpace = 0;
			int nbLines = 1;
			String promotionText = "";

			longestLinePromoText = 0;

			s_textPt = s_textPt.toUpperCase();

			char[] charArray = s_textPt.toCharArray();
			for (int i = 0; i < textLenght; i++)
			{
				char c = charArray[i];

				// Only the charactes in this range are admited 32(' ') to 122('z'), 130(',') and '\n'
				if( ((c >= 32) && (c <= 122)) || (c == 130) || (c=='\n') )
				{
					// Normal Line feed '\n'
					if (c == '\n')
					{
						newLineFound = true;
					}
					// Special Line feed '\N'
					else if ((i < (textLenght - 1)) && (c == '\\') && ((charArray[i + 1] == 'n') || (charArray[i + 1] == 'N')))
					{
						newLineFound = true;
						i++;
					}

					if (newLineFound)
					{
						// Ensure it's not the first char of the text
						if (promotionText.length() > 0)
						{
							nbLines ++;
							if (nbLines == MAX_URL_PT_LINES)
							{
								promotionText = null;
								break;
							}
							else
							{
								promotionText += '\n';
								if(occupiedTotal >longestLinePromoText)
								{
									longestLinePromoText = occupiedTotal;
								}
								occupiedTotal = 0;
							}
						}
						else
						{
							promotionText += "";
						}
						newLineFound = false;
					}
					else
					{
						// Add Character
						promotionText += c;                    
						occupiedTotal += getFontMetric(c, FONT_W);

						// Space
						if (c == 32)
						{
							lastSpace = promotionText.length() - 1;
							occupiedUntilLastSpace = occupiedTotal;
						}
					}           

					// Ensure the text doesn't spill out of the screen                
					if (occupiedTotal >= IGP_SCRW) // - (IGP_SCRW * 5) / 100)
					{
						if (nbLines < MAX_URL_PT_LINES)
						{
							if (lastSpace == i)
							{
								promotionText += "\n";
								occupiedTotal = 0;
							}
							else
							{
								// Ensure it's not a long word without spaces (else don't show it)
								if (lastSpace != 0)
								{
									promotionText = promotionText.substring(0, lastSpace) +
										"\n" + 
										promotionText.substring(lastSpace + 1, promotionText.length());
									occupiedTotal -= occupiedUntilLastSpace;
									
									if(occupiedUntilLastSpace >longestLinePromoText)
									{
										longestLinePromoText = occupiedUntilLastSpace;
									}
								}
								else
								{
									promotionText = null;
									break;
								}
							}                        
							nbLines ++;
						}
						else
						{
							promotionText = null;
							break;
						}
					}
				}
				else
				{
					promotionText = null;
					break;
				}
			}
			if(promotionText != null)
			{
				if (!checkURL(promotionText, k_Check_ALL))
				{
					promotionText = null;
				}
			}        
			s_textPt = promotionText;
			numberOfLinesPromoText = nbLines;
			
			if(occupiedTotal >longestLinePromoText)
			{
				longestLinePromoText = occupiedTotal;
			}
			
			//add a border to make it look better.
			longestLinePromoText += getFontMetric('O', FONT_W) * 2;
		}

		private static void loadResources(int loadStep)
		{
		#ifdef USE_NEW_RESOURCE_IGP
			switch (loadStep)
			{
				case IGP_TYPE_START:
					PageImages = new Image[IGPConfig.NUMBER_PAGE_IGP];
					GlobalImages = new Image[GLOBAL_IMAGE_TOTAL];
					break;
				case IGP_TYPE_LIBOPEN:
					cGame.Pack_Open(DATA.PACK_IGP);
					break;
				case IGP_TYPE_TEXTS:
					break;
				case IGP_TYPE_FONT:
					break;
				case IGP_TYPE_GLOBAL:
					if (IGPConfig.loadType != IGP.LOAD_RESOURCES_ON_PAGE_CHANGE)	
					{
						PageImages[3] = cGame.LoadImage(DATA.IGP_PAGE_IMAGE_4, false);
					}
					GlobalImages[GLOBAL_IMAGE_ARROW_LEFT_N] = cGame.LoadImage(DATA.IGP_ARROW_LEFT_N, false);
					GlobalImages[GLOBAL_IMAGE_ARROW_LEFT_H] = cGame.LoadImage(DATA.IGP_ARROW_LEFT, false);
					GlobalImages[GLOBAL_IMAGE_ARROW_RIGHT_N] = cGame.LoadImage(DATA.IGP_ARROW_RIGHT_N, false);
					GlobalImages[GLOBAL_IMAGE_ARROW_RIGHT_H] = cGame.LoadImage(DATA.IGP_ARROW_RIGHT, false);
					GlobalImages[GLOBAL_IMAGE_VISIT] = cGame.LoadImage(DATA.IGP_PRESS_VISIT, false);
					GlobalImages[GLOBAL_IMAGE_VISIT_V] = cGame.LoadImage(DATA.IGP_PRESS_VISIT_V, false);
					GlobalImages[GLOBAL_IMAGE_SELECTION] = cGame.LoadImage(DATA.IGP_SELECTION, false);				
					break;
				case IGP_TYPE_PROMOS:
					NumberOfPromos = 3;
					if (IGPConfig.loadType != IGP.LOAD_RESOURCES_ON_PAGE_CHANGE)
					{
						for (int i = 0; i < NumberOfPromos; i++)
						{
							PageImages[i] = cGame.LoadImage(DATA.IGP_PAGE_IMAGE_1 + i, false);
						}
					}
					else
					{
						PageImages[0] = cGame.LoadImage(DATA.IGP_PAGE_IMAGE_1, false);
					}
					break;
				case IGP_TYPE_PROMOTION:
					if (IGPConfig.loadType != IGP.LOAD_RESOURCES_ON_PAGE_CHANGE)	
						PageImages[6] = cGame.LoadImage(DATA.IGP_PAGE_IMAGE_7, false);
					break;
				case IGP_TYPE_WN:
					if (IGPConfig.loadType != IGP.LOAD_RESOURCES_ON_PAGE_CHANGE)	
						PageImages[4] = cGame.LoadImage(DATA.IGP_PAGE_IMAGE_5, false);
					break;
				case IGP_TYPE_BS:	
					if (IGPConfig.loadType != IGP.LOAD_RESOURCES_ON_PAGE_CHANGE)	
						PageImages[5] = cGame.LoadImage(DATA.IGP_PAGE_IMAGE_6, false);
					break;			
				default : 
					if (loadStep == IGP_TYPE_END) cGame.Pack_Close();
					break;
			}
		#else
			byte[] data = null;
			dataOffset = 0;

			switch (loadStep)
			{
				case IGP_TYPE_START:
					// init global arrays
					GlobalImages = new Image[DATA_IMG_COUNT];
					PageImages = new Image[numberOfPages];

					// LIST specific
					LIST_Images = new Image[NUMBER_OF_LIST][];
					LIST_Images[WN_LIST] = new Image[WNList.length];
					LIST_Images[BS_LIST] = new Image[BSList.length];
					LIST_Images[COMPLETE_CATALOG_LIST] = new Image[CatalogList.length];	
					break;

				case IGP_TYPE_LIBOPEN:
					// igp data file opening (read header)
					openDataIGP();
					break;

				case IGP_TYPE_TEXTS:
					data = readDataIGP(loadStep);

					int size;

					// skip languages
					for (int i = 0; i < CurrentLanguage; i++)
					{
						size = readInt(data);
						dataOffset += size;
					}

					readInt(data); // sizePack
					size = readInt(data);

					_StrMgr_Pack = new String[size];

					byte[] rawData = new byte[size];
					System.arraycopy(data, dataOffset, rawData, 0, size);

					dataOffset += size;
					readInt(data); // sg dumb...

					int nStrings = (data[dataOffset++] & 0xFF) | ((data[dataOffset++] & 0xFF) << 8);                

					_StrMgr_Offsets = new short[nStrings];

					for (int i = 0; i < (nStrings - 1); i++)
					{
						_StrMgr_Offsets[i] = (short) ((data[dataOffset++] & 0xFF) + ((data[dataOffset++] & 0xFF) << 8));
					}

					_StrMgr_Offsets[nStrings - 1] = (short) size;

					for (int i = 0; i < nStrings; i++)
					{
						int off = (i == 0) ? 0 : (_StrMgr_Offsets[i - 1] & 0xFFFF);
						int len = (_StrMgr_Offsets[i] & 0xFFFF) - off;

						if (len == 0)
						{
							continue;
						}

						try
						{
							if (IGPConfig.useUTF8Encoding)
							{
								_StrMgr_Pack[i] = new String(rawData, off, len, "UTF-8");						
							}
							else
							{
								StringBuffer strbuf = new StringBuffer(len / 2 + 2);
								for (int j = off; j < off + len; )
								{
									if ((rawData[j] & 0x80) == 0) // bit pattern 0xxxxxxx
									{
										strbuf.append((char) (rawData[j++] & 0xFF));
									}
									else if ((rawData[j] & 0xE0) == 0xC0) // bit pattern 110xxxxx
									{
										if ((j + 1 >= off + len) || (rawData[j + 1] & 0xC0) != 0x80)
										{
											throw new Exception();
										}
										strbuf.append((char) (((rawData[j++] & 0x1F) << 6) | (rawData[j++] & 0x3F)));
									}
									else if ((rawData[j] & 0xF0) == 0xE0) // bit pattern 1110xxxx
									{
										if ((j + 2 >= off + len) || (rawData[j + 1] & 0xC0) != 0x80 || (rawData[j + 2] & 0xC0) != 0x80)
										{
											throw new Exception();
										}
										strbuf.append((char) (((rawData[j++] & 0x0F) << 12) | ((rawData[j++] & 0x3F) << 6) | (rawData[j++] & 0x3F)));
									}
									else
									{
										// must be ((rawData [j] & 0xF0) == 0xF0 ||
										// (rawData [j] & 0xC0) == 0x80)
										throw new Exception ();  // bit patterns 1111xxxx or 10xxxxxx
									}
									_StrMgr_Pack[i] = strbuf.toString ().toUpperCase();
								}
							}
						}
						//#ifdef BLACKBERRY		
//@						catch (Throwable e)
							//#else			
							catch (Exception e)
							//#endif
							{
								//#ifdef DEBUG
//@								e.printStackTrace();
								//#else
								e = null;
								//#endif

							}
					}

					//#ifndef BLACKBERRY
					if (useCommandBar)
					{
						cmdSelect = new Command(getString(IGP_TEXT_SELECT), Command.OK, 1);
						cmdBack = new Command(getString(IGP_TEXT_BACK), Command.BACK, 1);

						setCommandBar(true, true);
					}
					//#endif				
					break;

				case IGP_TYPE_FONT:
					// fonts
					// font image with palette
					fontImage = new Image[NB_FONT_COLORS];
					data = readDataIGP(loadStep);

					byte dataClon[] = new byte[data.length];
					System.arraycopy(data, 0, dataClon, 0, data.length);

					int imageSize = readInt(data); // the image size
					dataOffset = 0;

					//fontImage[WHITE_FONT] = readImage(data);

					// fontImage[WHITE_FONT] = createFont(data, 2, imageSize, 1,/*0x0000b4ef*/0xFFFFFE);

					fontImage[WHITE_FONT] = readImage(data);
					
//					fontImage[BLUE_FONT] = createFont(dataClon, 2, imageSize, 1, 0x0000b4ef);
//					fontImage[DARK_BLUE_FONT] = createFont(dataClon, 2, imageSize, 1, 0x006e94a8);
					if (genericsSize.equals("128x"))
					{
						fontImage[BLUE_GRAY_FONT] = createFont(dataClon, 2, imageSize, 1, 0x3E545F);
					}
					else
					{
						fontImage[BLUE_GRAY_FONT] = createFont(dataClon, 2, imageSize, 1, 0x0088aabd);
					}
//					fontImage[DARK_FONT] = createFont(dataClon, 2, imageSize, 1, 0x001a2328);
//					fontImage[DARK_ORANGE_FONT] = createFont(dataClon, 2, imageSize, 1, LIST_SELECTION_COLOR);
					
					if (IGPConfig.useRedFonts)
					{
						//fontImage[RED_FONT] = createFont(dataClon, 2, imageSize, 1, 0x00FF0000);
						fontImage[RED_FONT]  = createFont(dataClon, 2, imageSize, 1, 0x00FF0000);
					}
					else
					{
						//fontImage[RED_FONT] = fontImage[WHITE_FONT];
						fontImage[RED_FONT]  = fontImage[WHITE_FONT];
					}

					dataClon = null;

					// font descriptor
					int descSize = readInt(data);

					int fontMaxCode = readInt(data);

					fontDescriptor = new byte[(fontMaxCode + 1) * (4 + fontCoordBytesFixedOffset)];

					//boolean isUnicode = fontMaxCode > 0xFF; // If a Language don't have Unicode Characters, the data-read
					//wasn't working and app crash on : System.arraycopy(data, dataOffset, fontDescriptor, imageNameInt, 4);
					//From now on, all string data is UNICODE

					//int nrChars = descSize / (isUnicode ? 6 : 5); // 5 or 6 bytes / char >> ascii or unicode, x,
					// y, w, h x 1 b
					int nrChars = descSize / (6 + fontCoordBytesFixedOffset);

					int imageNameInt;

					for (int i = 0; i < nrChars; i++)
					{
						//imageNameInt = isUnicode ? readInt(data) & 0xFFFF : readByte(data) & 0xFF;
						imageNameInt = readInt(data);
						imageNameInt = (imageNameInt * (4 + fontCoordBytesFixedOffset));
						System.arraycopy(data, dataOffset, fontDescriptor, imageNameInt, 4 + fontCoordBytesFixedOffset);
						dataOffset += (4 + fontCoordBytesFixedOffset);
					}

					fontHeight = fontDescriptor[(32 * (4 + fontCoordBytesFixedOffset)) + FONT_H]; //space * :
					DEFAULT_CHAR_SPACING = (fontHeight == 13 ? 0 : -1);

					loadPromotionText();
					break;

				case IGP_TYPE_GLOBAL:
					data = readDataIGP(loadStep);

					for (int i = 0; i < DATA_IMG_COUNT; i++)
					{
						// Skip sotkeys background for non touchscreen phones
						if (!IGPConfig.isTouchScreen && (i == IMG_BUTTON_BLUE || i == IMG_BUTTON_WHITE))
						{
							GlobalImages[i] = null;
						}
						if (i == IMG_ROUND_CORNER)
						{
							roundCorner = new Image[4];
							byte[] cornerData = getImageBytes(data);
							roundCorner[BLACK] = createImageFromPal(cornerData, 0, cornerData.length, 0x000000, 0x000000);
						    roundCorner[DARK_RED] = createImageFromPal(cornerData, 0, cornerData.length, 0x000000, (optionalFeatures[OPTIONAL_FEATURE_ORANGE_GLOBAL] ? 0xFF7F00 : 0xCF0900));
							roundCorner[PURPLE] = createImageFromPal(cornerData, 0, cornerData.length, 0x000000, 0x5800F2);
							roundCorner[LIGHT_BROWN] = createImageFromPal(cornerData, 0, cornerData.length, 0x000000, 0xDE9C00);
							GlobalImages[i] = roundCorner[BLACK];
						}
						else
						{
							GlobalImages[i] = readImage(data);
						}
					}
					if (right_sk_img != null)
					{
						GlobalImages[IMG_BACK] = right_sk_img; 
						GlobalImages[IMG_TICK] = left_sk_img; 
					}
					if (arrow_left_img != null)
					{
						GlobalImages[IMG_LEFTBLUE] = arrow_left_blink_img;
						GlobalImages[IMG_LEFTWHITE] = arrow_left_img;
						GlobalImages[IMG_RIGHTBLUE] = arrow_right_blink_img;
						GlobalImages[IMG_RIGHTWHITE] = arrow_right_img;
					}
					if (sk_background_img != null)
					{
						GlobalImages[IMG_BUTTON_BLUE] = sk_background_blink_img;
						GlobalImages[IMG_BUTTON_WHITE] = sk_background_img;
					}
					if (imgGameloftLogo != null)
					{
						GlobalImages[IMG_GAMELOFT] = imgGameloftLogo;
					}
					if (imgGameloftLogo != null)
					{
						GlobalImages[IMG_GAMELOFT] = imgGameloftLogo;
					}
					if (arrow_custom_left_img != null)
					{
					    GlobalImages[IMG_4BLUE] = arrow_custom_left_img;
					}
					if (arrow_custom_right_img != null)
					{
					    GlobalImages[IMG_6BLUE] = arrow_custom_right_img;
					}
					if (arrow_custom_left_blink_img != null)
					{
					    GlobalImages[IMG_4WHITE] = arrow_custom_left_blink_img;
					}
					if (arrow_custom_right_blink_img != null)
					{
					    GlobalImages[IMG_6WHITE] = arrow_custom_right_blink_img;
					}
					break;

				case IGP_TYPE_PROMOS:
					if (IGPConfig.loadType == IGP.LOAD_RESOURCES_ON_PAGE_CHANGE)
					{
						data = readDataIGP(loadStep);
						dataOffset = 0;

						int i = 0;
						while (i <= currentPage)
						{
							if (i == currentPage)
							{
								PageImages[currentPage] = readImage(data);
							}
							else
							{
								imageSize = readInt(data);
								dataOffset += imageSize;
							}
							i++;
						}
					}
					else
					{                
						for (int i = 0; i < NumberOfPromos; i++)
						{
							PageImages[i] = readImage(readDataIGP(loadStep));
						}
					}
					break;

				case IGP_TYPE_PROMOTION:
					data = readDataIGP(loadStep);
					byte [] imageInBytes = getImageBytes(data);
					PageImages[PAGE_PROMOTION] = CreateImage(imageInBytes, 0, imageInBytes.length);

					for (int i = 0; i < CurrentLanguage; i++)
					{
						readImage(data);
					}
					SpOfText = readImage(data);
					break;

				case IGP_TYPE_WN:                
				case IGP_TYPE_BS:
					int nbListItems = (loadStep == IGP_TYPE_WN ? WNList.length : BSList.length) - 1;
					int listType = (loadStep == IGP_TYPE_WN ? WN_LIST : BS_LIST);

					data = readDataIGP(loadStep);

					int i = 0;
					for (; i < nbListItems; i++)
					{
						LIST_Images[listType][i] = readImage(data);
					}
					LIST_Images[listType][i] = GlobalImages[IMG_MORE];
					PageImages[loadStep == IGP_TYPE_WN ? PAGE_WN : PAGE_BS] = GlobalImages[IMG_GAMELOFT];
					break;

				case IGP_TYPE_CATALOG:
					data = readDataIGP(loadStep);
					PageImages[PAGE_OPERATOR] = readImage(data);
					if( (AvailableLanguages[CurrentLanguage].equals("JP") || AvailableLanguages[CurrentLanguage].equals("DOCOMO")) && completeCatalogText == null)
					{
						completeCatalogText = readImage(data);
					}
					break;     

					// Optional Features are loaded here (chunks are dinamic)
				default:
					//#ifdef ENABLE_GLIVE
//@					if (loadStep == IGP_TYPE_OPTIONAL_FEATURES[OPTIONAL_FEATURE_GLLIVE])
//@					{
//@						/*
//@						 * // Make sure to load the star before changing the palette (LOAD_RESOURCES_ON_PAGE_CHANGE)
//@						 if (starImageInBytes == null)
//@						 {
//@						 loadResources(IGP_TYPE_PROMOTION);
//@						 dataOffset = 0;
//@						 data = null;
//@						 }
//@						 */
//@						data = readDataIGP(loadStep);
//@						GLiveImage = readImage(data);
//@						//PageImages[PAGE_GLIVE] = createImageFromPal(starImageInBytes, 0, starImageInBytes.length, 0x00FF3300, 0x00FFbd00);
//@						//PageImages[PAGE_GLIVE] = PageImages[PAGE_PROMOTION];
//@						PageImages[PAGE_GLIVE] = readImage(data);
//@					}
//@					else 
						//#endif
						if (loadStep == IGP_TYPE_OPTIONAL_FEATURES[OPTIONAL_FEATURE_ORANGE_GLOBAL])
						{
							data = readDataIGP(loadStep);
							PageImages[PAGE_ORANGE] = readImage(data);                     
						}
					//#ifdef ENABLE_VODA
//@						else if (loadStep == IGP_TYPE_OPTIONAL_FEATURES[OPTIONAL_FEATURE_VODAFONE])
//@						{
//@							if (bVodafoneCustomization)
//@							{
//@								data = readDataIGP(loadStep);
//@								imgVodafoneLogo = readImage(data);
//@							}
//@						}
					//#endif				 
						else if (loadStep == IGP_TYPE_END)
						{
							closeDataIGP();
						}
					break;
			}
		#endif //!USE_NEW_RESOURCE_IGP
		}

		private static void unloadResources(boolean complete)
		{
		#ifdef USE_NEW_RESOURCE_IGP //xuan
			for (int i = 0; i < PageImages.length; i++)
			{
				PageImages[i] = null;
			}
			if (complete)
			{
				for (int i = 0; i < GlobalImages.length; i++)
				{
					GlobalImages[i] = null;
				}
				GlobalImages = null;
			}
		#else

			for (int i = 0; i < LIST_Images.length; i++)
			{
				if (LIST_Images[i] != null)
				{
					for (int j = 0; j < LIST_Images[i].length; j++)
					{
						LIST_Images[i][j] = null;
					}
				}
			}
//#ifdef DEBUG
//@            log("unloadResources():  LIST_Images unloaded!");
//#endif            
			for (int i = 0; i < PageImages.length; i++)
			{
				PageImages[i] = null;
			}
//#ifdef DEBUG
//@			log("unloadResources():  PageImages unloaded!");
//#endif            
			//#ifdef ENABLE_GLIVE
//@			GLiveImage = null;
			//#endif		

			//#ifdef ENABLE_VODA
//@			// Optional Features resources must be unloaded always
//@			if (optionalFeatures[OPTIONAL_FEATURE_VODAFONE])
//@			{
//@				imgVodafoneLogo = null;
//@			}
			//#endif		

			SpOfText = null;
			promoTextTwist = null;
            completeCatalogText = null;

			if (complete)
			{
				closeDataIGP();
				fontDescriptor = null;
				fontImage = null;
				for (int i = 0; i < DATA_IMG_COUNT; i++)
				{
					GlobalImages[i] = null;
				}
				GlobalImages = null;
//#ifdef DEBUG
//@                log("unloadResources():  GlobalImages unloaded!");
//#endif
				_StrMgr_Offsets = null;
				_StrMgr_Pack = null;
				s_line_w = null;
				StringLoading = null;
				PageImages = null;
				LIST_Images = null;

				left_sk_img = null;
				right_sk_img = null;            
				//#if BLACKBERRY || BLACKBERRY_MIDP
//@				leftBB_sk_img = null;
//@				rightBB_sk_img = null;
//@				centerBB_sk_img = null;
				//#endif
				sk_background_img = null;
				sk_background_blink_img = null;
				arrow_left_blink_img = null;
				arrow_right_blink_img = null;
				arrow_left_img = null;
				arrow_right_img = null;
				arrow_custom_left_img = null;
	            arrow_custom_right_img = null;
	            arrow_custom_left_blink_img = null;
	            arrow_custom_right_blink_img = null;
				
				imgBackBuffer = null;
				imgGameloftLogo = null;
				for (int i = 0; i < promosSplashesOverriden.length; i++)
				{
					promosSplashesOverriden[i] = null;
				}
//#ifdef DEBUG
//@                log("unloadResources():  generics unloaded!");
//#endif
			}
			System.gc();
		#endif //USE_NEW_RESOURCE_IGP
//#ifdef DEBUG
//@            log("unloadResources() ended ok!");
//@            currentDebugSpacing = 0;
//#endif            
		}


		public static void notifyInterruption(boolean isInterrupted)
		{
			if(isInterrupted)
			{
				if(currentState == STATE_LOADING || currentState == STATE_INTERPAGE_LOADING)
				{
					lastState = currentState;
					currentState = STATE_INTERRUPTED;
				}
				if (IGPConfig.isTouchScreen)
				{
					resetTouchVariables();
				}
			}
			else
			{
				if( currentState == STATE_INTERRUPTED )
				{
					currentState = lastState;
					CurrentLoadingStep = -1;
				}
			}
		}


		private static int getPNGChunkOffset(byte[] buffer, int offset, int length, String chunkName)
		{
		    char[] charArray = chunkName.toCharArray();
			for (int i = offset; i < (length - 4); i++)
			{
				if (((buffer[i] & 0xFF) == charArray[0]) && ((buffer[i + 1] & 0xFF) == charArray[1]) && ((buffer[i + 2] & 0xFF) == charArray[2]) && ((buffer[i + 3] & 0xFF) == charArray[3]))
				{
					return i;
				}
			}

			return -1;
		}

		private static Image createImageFromPal(byte[] buffer, int offset, int length, int paletteColor, int finalColor)
		{
			long c;

			int[] indexes = new int[10];
			int[] temp_colors = new int[10];
			int index_count = 0;
			for (int i = 0; i < indexes.length; i++)
			{
				indexes[i] = -1;
				temp_colors[i] = -1;
			}

			int pngPlteOffset = getPNGChunkOffset(buffer, offset, length, "PLTE");
			int pngtRNSOffset = getPNGChunkOffset(buffer, offset, length, "tRNS");
			Image img = null;

			if ((pngPlteOffset > 0) && (pngtRNSOffset > 0))
			{
				int plteBytesSize = (int) ((buffer[pngPlteOffset - 4] << 24) & 0xFF000000) + (int) ((buffer[pngPlteOffset - 3] << 16) & 0x00FF0000) + (int) ((buffer[pngPlteOffset - 2] << 8) & 0x0000FF00) + (int) ((buffer[pngPlteOffset - 1] << 0) & 0x000000FF);

				boolean full_components_value = false;
				for (int i = 0; i < plteBytesSize / 3; i++)
				{
					if (buffer[pngtRNSOffset + 4 + i] != 0)
					{
						int r = buffer[pngPlteOffset + 4 + (3 * i)] & 0xFF;
						int g = buffer[pngPlteOffset + 4 + (3 * i) + 1] & 0xFF;
						int b = buffer[pngPlteOffset + 4 + (3 * i) + 2] & 0xFF;

						if (r == 0xFF || g == 0xFF || b == 0xFF)
						{
							full_components_value = true;
							break;
						}
						else if (r == 0x00 || g == 0x00 || b == 0x00)
						{
							full_components_value = true;
							break;
						}
					}
				}

				if (!full_components_value)
				{
					int a = ((paletteColor & 0xFF0000) >> 24) & 0xFF;
					int r = ((paletteColor & 0xFF0000) >> 16) & 0xFF;
					int g = ((paletteColor & 0x00FF00) >> 8) & 0xFF;
					int b = paletteColor & 0x0000FF;

					r = (r == 0xFF) ? 0xFE : r;
					g = (g == 0xFF) ? 0xFE : g;
					b = (b == 0xFF) ? 0xFE : b;

					r = (r == 0x00) ? 0x01 : r;
					g = (g == 0x00) ? 0x01 : g;
					b = (b == 0x00) ? 0x01 : b;

					paletteColor = 0;
					paletteColor |= ((a & 0xFF) << 24);
					paletteColor |= ((r & 0xFF) << 16);
					paletteColor |= ((g & 0xFF) << 8);
					paletteColor |= (b & 0xFF);

					log("fixPNG used for this dataIGP");
				}

				//search for color - make sure it's not fully transparent
				for (int i = 0; i < plteBytesSize / 3; i++)
				{
					if ((buffer[pngtRNSOffset + 4 + i] != 0) && //A - skip if fully transparent!
							((buffer[pngPlteOffset + 4 + (3 * i)] & 0xFF) == (((paletteColor & 0xFF0000) >> 16) & 0xFF)) && //R
							((buffer[pngPlteOffset + 4 + (3 * i) + 1] & 0xFF) == (((paletteColor & 0xFF00) >> 8) & 0xFF)) && //G
							((buffer[pngPlteOffset + 4 + (3 * i) + 2] & 0xFF) == (paletteColor & 0xFF))) //B
					{

						//replace the color
						indexes[index_count] = i;

						int tempColor = ArrayToInt(buffer, pngPlteOffset + 4 + (3 * i), 3);
						IntToArray(buffer, pngPlteOffset + 4 + (3 * i), 3, finalColor);

						temp_colors[index_count] = tempColor;
						index_count++;
					}
				}

				byte[] tmpBuf = new byte[plteBytesSize + 4];
				System.arraycopy(buffer, pngPlteOffset, tmpBuf, 0, plteBytesSize + 4);

				long[] crc_table = new long[256];

				for (int n = 0; n < 256; n++)
				{
					c = (long) n;

					for (int k = 0; k < 8; k++)
					{
						if ((c & 1) == 1)
						{
							c = 0xedb88320L ^ (c >> 1);
						}else
						{
							c = c >> 1;
						}
					}

					crc_table[n] = c;
				}

				// Return the CRC of the bytes buf[0..len-1]. 
				c = 0xffffffffL;

				for (int n = 0; n < tmpBuf.length; n++)
				{
					c = crc_table[(int) (c ^ tmpBuf[n]) & 0xff] ^ (c >> 8);
				}

				c ^= 0xffffffffL;

				//replace the crc
				int tempCRC = ArrayToInt(buffer, pngPlteOffset + 4 + plteBytesSize, 4);
				IntToArray(buffer, pngPlteOffset + 4 + plteBytesSize, 4, (int) c);

				tmpBuf = null;
				crc_table = null;
				System.gc();

				if (IGPConfig.createImageOffsetBug)
				{
					byte[] tempData = new byte[length];
					System.arraycopy(buffer, offset, tempData, 0, length);
					buffer = tempData;
					offset = 0;
				}

				img = Image.createImage(buffer, offset, length);

				//restore the data chunks affected by the color replacement
				for (int j = 0; j < index_count; j++)
				{
					int i = indexes[j];
					int tempColor = temp_colors[j];
					IntToArray(buffer, pngPlteOffset + 4 + (3 * i), 3, tempColor);
				}
				IntToArray(buffer, pngPlteOffset + 4 + plteBytesSize, 4, tempCRC);

			}

			return img;
		}

		private static void IntToArray(byte[] buffer, int startOffset, int nbBytes, int intValue)
		{
			for (int i = nbBytes - 1; i >= 0; i--)
			{
				buffer[(startOffset + nbBytes) - 1 - i] = (byte) ((intValue & (0xFF << (8 * i))) >> (8 * i));
			}
		}

		private static int ArrayToInt(byte[] buffer, int startOffset, int nbBytes)
		{
			int retVal = 0;

			for (int i = nbBytes - 1; i >= 0; i--)
			{
				retVal += ((buffer[(startOffset + nbBytes) - 1 - i] << (8 * i)) & (0xFF << (8 * i)));
			}

			return retVal;
		}

		// returns the current state
		public static int getCurrentState()
		{
			return currentState;
		}

		// must be called each frame during the IGP state
		public static boolean update(int action)
		{
		    if(useOVIFeature)
            {
                return true;
            }
			if (enterZVIP)
			{
				if (!isZVIPAvailable)
				{
					return true;
				}
				if (currentState == STATE_EXIT_IGP)
				{
					return true;
				}
				else
				{
					return false;
				}
			}

			// Safeguard for IGP and GLive
			if ( !isAvailable )
			{
				return true;
			}


			if (IGPConfig.isTouchScreen && isPointerReleased)
			{
				isPointerReleased = false;
			}
			else
			{
				currentAction = action;
			}		

			switch (currentState)
			{
				case STATE_LOADING:
					if (CurrentLoadingStep >= TotalLoadingSteps)
					{
                        //Now the strings are loaded and we cant calculate free space, fix lines according screen size
					#ifndef USE_NEW_RESOURCE_IGP
                        fixStringPack();
					#endif //!USE_NEW_RESOURCE_IGP
						currentState = STATE_PAGE;
						initPage();
					}
					else
					{
						loadResources(CurrentLoadingStep);
					}
					CurrentLoadingStep++;
					break;

				case STATE_PAGE:
					switch (currentAction)
					{
						case ACTION_BACK:
							currentState = STATE_EXIT_IGP;

							break;

						case ACTION_LEFT:
							if (ValidRealPages <= 1)
							{
								break;
							}

							if (currentPage == 0)
							{
								currentPage = numberOfPages - 1;
							}
							else
							{
								currentPage--;
							}

							while (!pagesValid[currentPage])
							{
								if (currentPage == 0)
								{
									currentPage = numberOfPages - 1;
								}
								else
								{
									currentPage--;
								}
							}
							_redArrowLeft = true;

						case ACTION_RIGHT:
							if (ValidRealPages <= 1)
							{
								break;
							}

							if (!_redArrowLeft) // right pressed
							{
								if (currentPage == numberOfPages - 1)
								{
									currentPage = 0;
								}
								else
								{
									currentPage++;
								}

								while (!pagesValid[currentPage])
								{
									if (currentPage == numberOfPages - 1)
									{
										currentPage = 0;
									}
									else
									{
										currentPage++;
									}
								}

								_redArrowRight = true;
							}

							s_igpListDisplayItemStart = 0;
							s_ItemListIndex = 0;
							initPage();
							break;

						case ACTION_DOWN:
							if (bIsListPage && (s_ItemListIndex < (currentList_nbItems - 1)))
							{
								s_ItemListIndex++;

								if ((s_ItemListIndex - s_igpListDisplayItemStart) >= LIST_visibleItemCount)
								{
									s_igpListDisplayItemStart++;
								}
							}
							break;

						case ACTION_UP:
							if (bIsListPage && (s_ItemListIndex > 0))
							{
								s_ItemListIndex--;

								if ((s_ItemListIndex - s_igpListDisplayItemStart) < 0)
								{
									s_igpListDisplayItemStart--;
								}
							}
							break;

						case ACTION_SELECT:
						case ACTION_SELECT_BOX:
							currentState = STATE_PLATFORM_REQUEST;
							break;
					}
					break;

				case STATE_PLATFORM_REQUEST:
					String url = null;
					url = pagesURLs[currentPage];

					if (bIsListPage)
					{
						url = LIST_URLs[currentList][s_ItemListIndex];
					}

					// URL for platform request set
					if ((url != null) && (url.length() > 0))
					{
						// Add Language id tracking, only when it's redirection
						if (b_useIGPRedir)
						{
							int langugeIndex = url.indexOf(s_LANGUAGE);
							if (langugeIndex == -1)
							{
								url += s_LANGUAGE + AvailableLanguages[CurrentLanguage];
							}
							else
							{
								url = url.substring(0, langugeIndex) + s_LANGUAGE + AvailableLanguages[CurrentLanguage] + url.substring(langugeIndex + s_LANGUAGE.length() + 2);
							}
						}                    

						//#ifdef SPRINT
//@						currentState = STATE_ASKING_TO_CLOSE_APP;
//@						needRedraw = true;
//@						URLPlatformRequestPending = url;
						//#else
						if (IGPConfig.platformRequestType == PLATFORM_REQUEST_ON_NEW_THREAD)
						{
							IGPServerURL = url;
						}
						else
						{
							URLPlatformRequest = url;
						}
						//#endif
					}
					break;

				case STATE_INTERPAGE_LOADING:
					// unload previous page resources
					unloadResources(false);
				#ifdef USE_NEW_RESOURCE_IGP
					cGame.Pack_Open(DATA.PACK_IGP);
					PageImages[0] = cGame.LoadImage(DATA.IGP_PAGE_IMAGE_1 + currentPage, false);
					cGame.Pack_Close();
				#else //!USE_NEW_RESOURCE_IGP
					if (IGPConfig.loadType == LOAD_RESOURCES_ON_PAGE_CHANGE)
					{
						openDataIGP();
					}

					// Get Loading Step according to the current page
					CurrentLoadingStep = getPageChunkIndex(currentPage);
					loadResources(CurrentLoadingStep);

					//#ifdef ENABLE_VODA
//@					// chunkVodafone and chunkPromotion are different, but need to be loaded together if it's Vodafone
//@					if (currentPage == PAGE_PROMOTION && optionalFeatures[OPTIONAL_FEATURE_VODAFONE])
//@					{
//@						loadResources(IGP_TYPE_OPTIONAL_FEATURES[OPTIONAL_FEATURE_VODAFONE]);
//@					}
					//#endif
					if (IGPConfig.loadType == LOAD_RESOURCES_ON_PAGE_CHANGE )
					{
						closeDataIGP();
					}
                    fixStringPack();
				#endif //!USE_NEW_RESOURCE_IGP
					currentState = STATE_PAGE;
					break;

				case STATE_INTERRUPTED:
					break;

				case STATE_ASKING_TO_CLOSE_APP:
					switch (currentAction)
					{
						case ACTION_BACK:
							currentState = STATE_PAGE;
							URLPlatformRequestPending = null;

							break;

						case ACTION_SELECT:
							if (IGPConfig.platformRequestType == PLATFORM_REQUEST_ON_NEW_THREAD)
							{
								IGPServerURL = URLPlatformRequestPending;
							}
							else
							{
								URLPlatformRequest = URLPlatformRequestPending;
							}
							break;
					}
					break;

				case STATE_EXIT_IGP:
					// exit , unload all resources			
					unloadResources(true);			
					//#ifndef BLACKBERRY
					if(useCommandBar)
					{
						GameInstance.setCommandListener( GameCmdListener );
						removeCommands();
					}
					//#endif				

					IGPRunning = false;
					return true;
			}
			return false;
		}

		// call in your pointer events if you plan on supporting touch
		public static void updatePointerReleased(int x, int y)
		{
			int actionTouch = updateTouchIGP(x, y);

			if (actionTouch != ACTION_NONE)
			{
				/* Uncomment this to avoid the one touch action
				   lastTouchActionReleased = actionTouch;        	

				// if the touch is released in the same location as the pressed then perform the action
				if (lastTouchActionPressed == lastTouchActionReleased)
				{
				currentAction = lastTouchActionReleased;

				//Only for list pages, check dragging origin and destiny
				if(isDragging && ItemListBeforeDrag != s_ItemListIndex)
				{
				currentAction = ACTION_NONE;
				}
				isDragging = false;
				isPointerReleased = true;
				}
				*/

				currentAction = actionTouch;
				isDragging = false;
				isPointerReleased = true;
			}
			lastTouchActionPressed = ACTION_NONE;
			lastTouchActionReleased = ACTION_NONE;
			isOKSoftkeyBeenTouched = false;
		}

		// call in your pointer events if you plan on supporting touch
		public static void updatePointerPressed(int x, int y)
		{
			int actionTouch = updateTouchIGP(x, y);

			if (actionTouch != ACTION_NONE)
			{
				if(bIsListPage)
				{
					ItemListBeforeDrag = s_ItemListIndex;
				}
				lastTouchActionPressed = actionTouch;
			}
		}

		// call in your pointer events if you plan on supporting touch
		public static void updatePointerDragged(int x, int y)
		{
			currentAction = ACTION_NONE;
			lastTouchActionPressed = ACTION_NONE;
			isOKSoftkeyBeenTouched = false;
			IGP.updatePointerPressed(x, y);
		}   


		// check collisions and return an IGP action
		private static int updateTouchIGP(int x, int y) //Updates the effect that IGP has when the screen is touched
		{
			if (!IGPConfig.isTouchScreen)
			{
				return ACTION_NONE;
			}

			// this will be modified the (x,y) depending on the rotation selected.
		    if (IGPConfig.screenRotation == IGPConfig.SCREEN_ROTATION_90)
            {
                int aux = x;
                x = y;
                y = aux;
                y = IGP_SCRH - y;
            }
            
            if (IGPConfig.screenRotation == IGPConfig.SCREEN_ROTATION_180)
            {
                x = IGP_SCRW - x;
                y = IGP_SCRH - y;
            }

            if (IGPConfig.screenRotation == IGPConfig.SCREEN_ROTATION_270)
            {
                int aux = x;
                x = y;
                y = aux;
                x = IGP_SCRW - x;
            }

			// Safeguard: x, y out of the screen
			if ((x < 0) || (y < 0))		
			{
				return ACTION_NONE;
			}

			// Safeguard: Avoid touch when loading
			if (currentState == STATE_LOADING || !IGPRunning)
			{
				return ACTION_NONE;
			}

			if(bIsListPage
			//#ifdef SPRINT
//@			&& currentState != STATE_ASKING_TO_CLOSE_APP
			//#endif
			)
			{
				//Handling of touchscreen events while page has lists.
				int vertical_arrow_height = (IGP_SCRH * 5) / 100;
				int list_item_offset_x = 1 + vertical_arrow_height + GlobalImages[IMG_LEFTWHITE].getWidth();
				int list_item_text_offset_x = (IGP_SCRW * 1) / 100; // 1% offset

				int list_x = list_item_offset_x + LIST_Images[currentList][0].getWidth() + list_item_text_offset_x;

                list_item_text_offset_x *= 3;//3% of screen
				boolean x_position = (x > (listSelector_x + list_item_text_offset_x) ) && (x < (listSelector_x + listSelector_w - list_item_text_offset_x));

				for (int i = s_igpListDisplayItemStart; i < (s_igpListDisplayItemStart + LIST_visibleItemCount); i++)
				{
					if (x_position)
					{
						//if ((y >= listSelector_y) && (y <= listSelector_y + listSelector_h)) 
						if ((y > (Y_TouchPosition + (listSelector_h * (i - s_igpListDisplayItemStart)))) && (y < ((Y_TouchPosition + (listSelector_h * (i - s_igpListDisplayItemStart))) + listSelector_h)))
						{
							if (s_ItemListIndex == i)
							{
								return ACTION_SELECT;
							}
							else
							{
								s_ItemListIndex = i;

								return ACTION_NONE;
							}
						}
					}
				}

				
                //check softkeys
                if(x_position)
                {
                    x_position =  x > (GlobalImages[IMG_BUTTON_WHITE].getWidth()*3)/2+ IGPConfig.leftSoftkeyXOffsetFromBorders
                               && x < IGP_SCRW - ( (GlobalImages[IMG_BUTTON_WHITE].getWidth()*3)/2 - IGPConfig.rightSoftkeyXOffsetFromBorders);
                }
                
                //up & down arrows from List
				if (LIST_visibleItemCount < currentList_nbItems)
				{
					if (x_position)
					{
						//UP arrow
						if (s_ItemListIndex > 0)
						{
							if ((y > (Y_littleArrow_Up - ((list_x - 2) / 2))) && (y < (Y_littleArrow_Up + (list_x - 2))))
							{
								return ACTION_UP;
							}
						}

						//Down arrow
						if (s_ItemListIndex < (currentList_nbItems - 1))
						{
							if ((y > (Y_littleArrow_Down - ((list_x - 2) / 2))) && (y < (Y_littleArrow_Down + (list_x - 2)))) //s_ItemListIndex - s_igpListDisplayItemStart < 0)
							{
								return ACTION_DOWN;
							}
						}
					}
				}
			}
			else
			{
				//Other pages - ( Flashing Box )
				if ((x > box_x) && (x < (box_x + box_w)))
				{
					if ((y > box_y) && (y < (box_y + box_h)))
					{
						return ACTION_SELECT_BOX;
					}
				}

				if (IGPConfig.useBiggerTouchAreas)
				{
					if ((x > TOUCH_AREA_X) && (x < (TOUCH_AREA_X + TOUCH_AREA_W)))
					{
						if ((y > TOUCH_AREA_Y) && (y < (TOUCH_AREA_Y + TOUCH_AREA_H)))
						{
							return ACTION_SELECT_BOX;
						}
					}
				}


			}

			// Generic for all pages		
			int x_arrow4 = 0;
			int x_arrow6 = IGP_SCRW - (GlobalImages[IMG_RIGHTWHITE].getWidth());
			int y_arrows = (IGP_SCRH >> 1) - (GlobalImages[IMG_RIGHTWHITE].getHeight());		
			int arrowHeight = GlobalImages[IMG_RIGHTWHITE].getHeight() * 2;
			int arrowWidth = GlobalImages[IMG_RIGHTWHITE].getWidth() * 2;		
			final boolean invertSoftkeys = IGPConfig.softkeyOKOnLeft;

			if ((y >= y_arrows) && (y <= (y_arrows + arrowHeight)))
			{
				if ((x >= x_arrow4) && (x <= (x_arrow4 + arrowWidth)))
				{
					return ACTION_LEFT;
				}
				else if ((x >= (x_arrow6 - (arrowWidth / 2))) && (x <= (x_arrow6 + arrowWidth)))
				{
					return ACTION_RIGHT;
				}
			}

            //Determine the touch area size
            int areaSoftkeyOffset = IGPConfig.useBiggerTouchAreas?2:1;

			if (useCommandBar)
			{
				return ACTION_NONE;
			}
			else if ((y >= IGP_SCRH - (GlobalImages[IMG_BUTTON_BLUE].getHeight() * areaSoftkeyOffset)) && (y <= IGP_SCRH))
			{
				if ((x > IGP_LSK_X) && (x < (GlobalImages[IMG_BUTTON_BLUE].getWidth()* areaSoftkeyOffset) + IGP_LSK_X))
				{
					if (invertSoftkeys)
					{
						if (IGPConfig.removeSelectSoftkey)
						{
							return ACTION_NONE;
						}
						else
						{
							isOKSoftkeyBeenTouched = true;
							return ACTION_SELECT;
						}
					}
					else
					{
						if (IGPConfig.removeBackSoftkey)
						{
							return ACTION_NONE;
						}
						else
						{
							return ACTION_BACK;
						}
					}
				}

				if ((x > IGP_SCRW - IGP_RSK_X - (GlobalImages[IMG_BUTTON_BLUE].getWidth() * areaSoftkeyOffset)) && (x < (IGP_SCRW - IGP_RSK_X)))
				{
					if (invertSoftkeys)
					{
						if (IGPConfig.removeBackSoftkey)
						{
							return ACTION_NONE;
						}
						else
						{
							return ACTION_BACK;
						}
					}
					else
					{
						if (IGPConfig.removeSelectSoftkey)
						{
							return ACTION_NONE;
						}
						else
						{
							isOKSoftkeyBeenTouched = true;
							return ACTION_SELECT;
						}
					}
				}
			}

			//Default action		
			return ACTION_NONE;
		}

		private static void resetTouchVariables()
		{
			currentAction = ACTION_NONE;
			lastTouchActionPressed = ACTION_NONE;
			lastTouchActionReleased	= ACTION_NONE;
			isPointerReleased = true;
			isOKSoftkeyBeenTouched = false;
			isDragging = false;
		}

		private static void initPage()
		{
			if (IGPConfig.loadType == LOAD_RESOURCES_ON_PAGE_CHANGE)
			{
				currentState = STATE_INTERPAGE_LOADING;
			}
			pageTextId = currentPage;

			s_ItemListIndex = 0;
			currentList_nbItems = 0;
			s_igpListDisplayItemStart = 0;
			bIsListPage = false;
			bDisplayButton = (pagesURLs[currentPage] != null) && (pagesURLs[currentPage].length() > 0) && (pagesURLs[currentPage].compareTo("DEL") != 0);

			if (currentPage == PAGE_WN)
			{
				currentList = WN_LIST;
				bIsListPage = true;
				bDisplayButton = false;
			}
			if (currentPage == PAGE_BS)
			{
				currentList = BS_LIST;
				bIsListPage = true;
				bDisplayButton = false;
			}
			if (currentPage == PAGE_OPERATOR || currentPage == PAGE_ORANGE)
			{
				bDisplayButton = true;
			}

			iButtonStringId = IGPConfig.isTouchScreen ? IGP_TEXT_TOUCHVISIT : (IGPConfig.usePressOKInsteadOfPress5 ? IGP_TEXT_PRESS_OK_TO_VISIT : IGP_TEXT_PRESS_5_TO_VISIT);

			if (pagesType[currentPage] == IGP_TYPE_PROMOS)
			{
				iButtonStringId = IGPConfig.isTouchScreen ? IGP_TEXT_TOUCHGET : (IGPConfig.usePressOKInsteadOfPress5 ? IGP_TEXT_PRESS_OK_TO_GET_IT : IGP_TEXT_PRESS_5_TO_GET_IT);
			}
			//#ifdef ENABLE_VODA
//@			if (currentPage == PAGE_PROMOTION && bVodafoneCustomization && AvailableLanguages[CurrentLanguage].equals("SP"))
//@			{
//@				iButtonStringId = IGPConfig.isTouchScreen ? IGP_TEXT_TOUCH_VODAFONE : IGP_TEXT_5_VODAFONE;
//@			}
			//#endif		

			if (bIsListPage)
			{
				currentList_nbItems = LIST_nbItems[currentList];
			}
		}
		
		public static void flipScreen()
		{
			int lastWidth = IGP_SCRW;
		
			IGP_SCRW = IGP_SCRH;
			IGP_SCRH = lastWidth;
			
			IGP_HALF_SCRW = (IGP_SCRW >> 1);
			IGP_HALF_SCRH = (IGP_SCRH >> 1);
			
			GAMELOFT_LOGO_Y = (IGP_SCRH * 5) / 100; // 5%
			PRESS5_TO_GETIT_Y = IGP_SCRH / 2;
			PRESS5_TO_VISIT_Y = (IGP_SCRH * 93) / 100; // 93%
			
			if(currentState == STATE_PAGE)
			{
				initPage();
			}
		#ifndef USE_NEW_RESOURCE_IGP
			fixStringPack();
		#endif
		}

		// must be called each frame during the IGP state
		public static void paint(Graphics g)
		{
		    if(useOVIFeature)
            {
                return;
            }
			if(IGPConfig.flipScreenOnWidthChange && GameInstance.getWidth() != IGP_SCRW)
			{
				flipScreen();
			}
		
			// Safeguard for IGP and GLive
			if (!isAvailable && !enterZVIP)
			{
				return;
			}					

			if ((URLPlatformRequest != null) && (IGPConfig.platformRequestType == PLATFORM_REQUEST_ON_PAINT)
			//#ifdef SPRINT
//@			&& lastState != STATE_ASKING_TO_CLOSE_APP
			//#endif
			)
			{
				doPlatformRequest();

				return;
			}

			if (enterZVIP)
			{
				return;
			}

			// Initialize IGP graphics.
			Graphics _gBB;
			if (IGPConfig.screenRotation != IGPConfig.SCREEN_ROTATION_NONE)
			{
				if(imgBackBuffer == null)
				{
					return;
				}
			    _gBB = imgBackBuffer.getGraphics();
			    SetClip(g, 0, 0, IGP_SCRW, IGP_SCRH);
			    SetClip(_gBB, 0, 0, IGP_SCRW, IGP_SCRH);
			}
			else
			{
			    _gBB = g;
			    SetClip(g, 0, 0, IGP_SCRW, IGP_SCRH);
			    SetClip(_gBB, 0, 0, IGP_SCRW, IGP_SCRH);
			}

			switch (currentState)
			{
				case STATE_LOADING:
				    
					g.setColor(0);
					g.fillRect(0, 0, IGP_SCRW, IGP_SCRH);
					drawProgressBar(g, IGP_HALF_SCRH, (IGP_SCRW * 3) / 4, CurrentLoadingStep, TotalLoadingSteps);

					_gBB.setColor(0);
					_gBB.fillRect(0, 0, IGP_SCRW, IGP_SCRH);
					drawProgressBar(_gBB, IGP_HALF_SCRH, (IGP_SCRW * 3) / 4, CurrentLoadingStep, TotalLoadingSteps);

					if ((StringLoading == null) || StringLoading.trim().equals(""))
					{
						break; // default value!
					}

					g.setColor(0xFFFFFF);
					g.setFont(sysFont);
					g.drawString(StringLoading, IGP_HALF_SCRW, IGP_HALF_SCRH - LOADING_INTER_SPACE, Graphics.BOTTOM | Graphics.HCENTER);

					_gBB.setColor(0xFFFFFF);
					_gBB.setFont(sysFont);
					_gBB.drawString(StringLoading, IGP_HALF_SCRW, IGP_HALF_SCRH - LOADING_INTER_SPACE, Graphics.BOTTOM | Graphics.HCENTER);
					break;

				case STATE_PAGE:
					drawPage(_gBB);

					// draw flashing box in the middle
					if ((System.currentTimeMillis() % BLINK_TIME) > (BLINK_TIME >> 1) || (lastTouchActionPressed == ACTION_SELECT_BOX && !isOKSoftkeyBeenTouched))
					{
						drawFlashingBox(g);
						drawFlashingBox(_gBB);
					}
					break;

				case STATE_INTERPAGE_LOADING:
				#ifdef USE_NEW_RESOURCE_IGP //xuan
					int textH = IGPConfig.FONT_HEIGHT;//xuan
				#else
					int textH = sysFont.getHeight();
				#endif

					g.setColor(0x608294);
					g.fillRect(0, IGP_HALF_SCRH - textH - LOADING_INTER_SPACE, IGP_SCRW, textH * 2);

					_gBB.setColor(0x608294);
					_gBB.fillRect(0, IGP_HALF_SCRH - textH - LOADING_INTER_SPACE, IGP_SCRW, textH * 2);

					int previousDrawColor = drawColor;
                    drawColor = WHITE_FONT;
				#ifdef USE_NEW_RESOURCE_IGP //xuan					
					drawString("LOADING",g, IGP_SCRW, IGP_HALF_SCRW, IGP_HALF_SCRH - textH, Graphics.BASELINE | Graphics.HCENTER);
					
                    drawString("LOADING",_gBB, IGP_SCRW, IGP_HALF_SCRW, IGP_HALF_SCRH - textH, Graphics.BASELINE | Graphics.HCENTER);
				#else	
                    drawString(IGP_TEXT_LOADING,g, IGP_HALF_SCRW, IGP_HALF_SCRH - textH, Graphics.BASELINE | Graphics.HCENTER);
					
                    drawString(IGP_TEXT_LOADING,_gBB, IGP_HALF_SCRW, IGP_HALF_SCRH - textH, Graphics.BASELINE | Graphics.HCENTER);
				#endif
                    drawColor = previousDrawColor;
					break;

				case STATE_INTERRUPTED:

					break;

				case STATE_ASKING_TO_CLOSE_APP:

					drawPage(_gBB);

					int height = (IGP_SCRH*40)/100;
					int width = IGP_SCRW;

					//#ifndef MIDP1
					if (IGPConfig.useAlphaBanners)
					{
						//if (needRedraw)
						{	
							fillAlphaRect(g, 0, height, width, IGP_SCRH - height*2, ASK_TRANS_BKG_COLOR);

							fillAlphaRect(_gBB, 0, height, width, IGP_SCRH - height*2, ASK_TRANS_BKG_COLOR);
							needRedraw = false;
						}
					}
					else
					//#endif	
					{
						g.setColor(ASK_BKG_COLOR);
						g.fillRect(0, height, width, IGP_SCRH - height*2);

						_gBB.setColor(ASK_BKG_COLOR);
						_gBB.fillRect(0, height, width, IGP_SCRH - height*2);
					}				
					g.setColor(GLIVE_BLUE);
					g.drawRect(0, height, width-1, IGP_SCRH - height*2 - 1);
					g.drawRect(1, height+1, width-3, IGP_SCRH - height*2 - 3);
					drawString(IGP_EXIT_CONFIRM, g, IGP_HALF_SCRW, IGP_HALF_SCRH, Graphics.VCENTER | Graphics.HCENTER);
					drawSoftkeys(g);

					_gBB.setColor(GLIVE_BLUE);
					_gBB.drawRect(0, height, width-1, IGP_SCRH - height*2 - 1);
					_gBB.drawRect(1, height+1, width-3, IGP_SCRH - height*2 - 3);
					drawString(IGP_EXIT_CONFIRM, _gBB, IGP_HALF_SCRW, IGP_HALF_SCRH, Graphics.VCENTER | Graphics.HCENTER);
					drawSoftkeys(_gBB);
					break;
			}


			if (IGPConfig.screenRotation != IGPConfig.SCREEN_ROTATION_NONE)
			{
			    int transform = IGPConfig.SCREEN_ROTATION_NONE;
			    
			    if (IGPConfig.screenRotation == IGPConfig.SCREEN_ROTATION_180)
                {
			        g.setClip(0, 0, IGP_SCRW, IGP_SCRH);
                    transform = Sprite.TRANS_ROT180;
                }
			    else
			    {
			        g.setClip(0, 0, IGP_SCRH, IGP_SCRW);
			        if (IGPConfig.screenRotation == IGPConfig.SCREEN_ROTATION_90)
                    {
                        transform = Sprite.TRANS_ROT90;
                    }
			        else if (IGPConfig.screenRotation == IGPConfig.SCREEN_ROTATION_270)
	                {
	                    transform = Sprite.TRANS_ROT270;
	                }
			    }
			    
                int STEPS_W = IGPConfig.screenRotBufferW;
                int STEPS_H = IGPConfig.screenRotBufferH;
                int stepSizeW = imgBackBuffer.getWidth() / STEPS_W + imgBackBuffer.getWidth() % STEPS_W;
                int stepSizeH = imgBackBuffer.getHeight() / STEPS_H + imgBackBuffer.getHeight() % STEPS_H;
                    
                switch(IGPConfig.screenRotation)
                {
                    case IGPConfig.SCREEN_ROTATION_90:
                        for(int i = 0; i < STEPS_W; i++)
                        {
                            for(int j = 0; j < STEPS_H; j++)
                            {
                                g.drawRegion(imgBackBuffer, i * stepSizeW, j * stepSizeH, stepSizeW, stepSizeH, transform, (STEPS_H - j - 1) * stepSizeH, i * stepSizeW, 0);
                            }
                        }
                    break;
                    
                    case IGPConfig.SCREEN_ROTATION_180:
                        for(int i = 0; i < STEPS_W; i++)
                        {
                            for(int j = 0; j < STEPS_H; j++)
                            {
                                g.drawRegion(imgBackBuffer, i * stepSizeW, j * stepSizeH, stepSizeW, stepSizeH, transform, (STEPS_W - i - 1) * stepSizeW, (STEPS_H - j - 1) * stepSizeH, 0);
                            }
                        }
                    break;
                    
                    case IGPConfig.SCREEN_ROTATION_270:
                        for(int i = 0; i < STEPS_W; i++)
                        {
                            for(int j = 0; j < STEPS_H; j++)
                            {
                                g.drawRegion(imgBackBuffer, j * stepSizeW, i * stepSizeH, stepSizeW, stepSizeH, transform, i * stepSizeH, (STEPS_H - j - 1) * stepSizeW, 0);
                            }
                        }
                    break;
                }
			}
		}

		//#ifndef MIDP1
		private static void fillAlphaRect(Graphics gfx, int x, int y, int w, int h, int color)
		{
			int[] data = new int[w*h];

			for (int i=0; i < data.length; i++)
			{
				data[i] = color;
			}
			gfx.drawRGB(data, 0, w, x, y, w, h, true);
		}
		//#endif

		private static void drawPage(Graphics _gBB)
		{
			int softkeys_y;
			
			getFlashingBoxRect();

			// Fill screen with background color
			//#ifdef ENABLE_GLIVE				
//@			if (currentPage == PAGE_GLIVE)
//@			{
//@				_gBB.setColor(GLIVE_BLUE);
//@			}
//@			else
				//#endif
			{
				_gBB.setColor(BK_COLOR);

				if (optionalFeatures[OPTIONAL_FEATURE_ORANGE_GLOBAL])
				{
					_gBB.setColor(0);
				}
			}

			if (backGroundImage != null)
			{
				_gBB.drawImage(backGroundImage, 0, 0, 0);
			}
			else
			{
				if (IGPConfig.useDetailedBackgrounds)
				{
					drawGradient(_gBB, 0, 0, IGP_SCRW, IGP_SCRH, 0x000000, GRADIENT_BOTTOM, false);
				}
				else
				{
					_gBB.setColor(0x2d3d45);
					_gBB.fillRect(0, 0, IGP_SCRW, IGP_SCRH);
				}
			}

			// Draw back/tick softkeys
			drawSoftkeys(_gBB);
			softkeys_y = IGP_SCRH - GlobalImages[(IGPConfig.isTouchScreen ? IMG_BUTTON_WHITE : IMG_TICK)].getHeight() - softkeysAdditionalSpace;

			// Draw Pages
			if (bIsListPage)
			{
				drawList(_gBB);
			}
			else
			{
				if (currentPage == PAGE_ORANGE)
				{
				#ifdef USE_NEW_RESOURCE_IGP //xuan
					if (IGPConfig.loadType == LOAD_RESOURCES_ON_PAGE_CHANGE)
						DrawImage(_gBB, PageImages[0], 0, 0, 0);
					else
						DrawImage(_gBB, PageImages[PAGE_ORANGE], 0, 0, 0);
					drawString("PRESS 5 TO VISIT", _gBB, IGP_SCRW - ((GlobalImages[IMG_LEFTBLUE].getWidth() + SIDE_ARROWS_ANIMATION_WIDTH) * 2), IGP_SCRW / 2, (IGP_SCRH * 60) / 100, Graphics.HCENTER | Graphics.VCENTER);
				#else
					int logo_offset_x = (IGP_SCRW / 2);
                    int logo_offset_y = GAMELOFT_LOGO_Y;

                    // Draw orange image.
                    DrawImage(_gBB, PageImages[PAGE_ORANGE], logo_offset_x, logo_offset_y, Graphics.TOP | Graphics.HCENTER);

					bDetermineSizeOnly = true;
					drawString(IGP_TEXT_CATALOG_ORANGE, null, 0, 0, 0);

					int text_height = s_text_h;
					int text_area_height = box_y - PageImages[currentPage].getHeight();
					int text_padding = Math.abs((text_area_height - text_height) / 3);
					text_padding = text_padding < 3 ? 3: text_padding;

					if (optionalFeatures[OPTIONAL_FEATURE_ORANGE_GLOBAL])
					{
						drawColor = WHITE_FONT;
					}

					// Visit your Portal string
					drawString(getString(IGP_TEXT_VISIT_YOUR_PORTAL), _gBB,
					        IGP_SCRW - ((GlobalImages[IMG_LEFTBLUE].getWidth() + SIDE_ARROWS_ANIMATION_WIDTH) * 2),
					        (IGP_SCRW >> 1),
					        (IGP_SCRH >> 1),
					        Graphics.HCENTER | Graphics.VCENTER);
				#endif
				}
				else if (currentPage == PAGE_PROMOTION)
				{
				#ifdef USE_NEW_RESOURCE_IGP //xuan
				if (IGPConfig.loadType == LOAD_RESOURCES_ON_PAGE_CHANGE)
					DrawImage(_gBB, PageImages[0], 0, 0, 0);				
				else
					DrawImage(_gBB, PageImages[PAGE_PROMOTION], 0, 0, 0);				
				#else
					boolean draw_logo = IGP_SCRH >= 128;
					boolean draw_text = (s_textPt != null) && (s_textPt.length() > 0);
					int offset_y = 0;
					
					//#ifdef ENABLE_VODA						
//@					int logo_height = draw_logo ? (bVodafoneCustomization ? imgVodafoneLogo.getHeight() : GlobalImages[IMG_GAMELOFT].getHeight()) : 0;
					//#else
					int logo_height = draw_logo ? GlobalImages[IMG_GAMELOFT].getHeight() : 0;
					//#endif
					
					if (canDrawHeader)
					{
						drawHeader(_gBB, false);
						offset_y = 2 * GAMELOFT_LOGO_Y + logo_height + 1;
					}

					// * Draw Page *

					//Draw Promo image + rotated text + cord
					if( PageImages[PAGE_PROMOTION].getWidth() > 220) //image width is 224 -> resources 320x
					{
						_gBB.setColor(CORD_COLOUR_320);
						_gBB.fillRect(IGP_HALF_SCRW + CORD_OFFSET_X_320, offset_y, CORD_WIDTH_320, CORD_OFFSET_Y_320);
						offset_y += CORD_OFFSET_Y_320;
					}
					else if ( PageImages[PAGE_PROMOTION].getWidth() > 140) //image width is 147 -> resources 240x or 320w
					{
						_gBB.setColor(CORD_COLOUR_240);
						_gBB.fillRect(IGP_HALF_SCRW + CORD_OFFSET_X_240, offset_y, CORD_WIDTH_240, CORD_OFFSET_Y_240);
						offset_y += CORD_OFFSET_Y_240;
					}
					else if ( PageImages[PAGE_PROMOTION].getWidth() > 100) //image width is 109 -> resources 176x or 240w
					{
						_gBB.setColor(CORD_COLOUR_176);
						_gBB.fillRect(IGP_HALF_SCRW + CORD_OFFSET_X_176, offset_y, CORD_WIDTH_176, CORD_OFFSET_Y_176);
						offset_y += CORD_OFFSET_Y_176;
					}

					offset_y += PageImages[PAGE_PROMOTION].getHeight() - 1;
					DrawImage(_gBB, PageImages[PAGE_PROMOTION], IGP_HALF_SCRW, offset_y, Graphics.BOTTOM | Graphics.HCENTER);
					DrawImage(_gBB, SpOfText, IGP_HALF_SCRW + (PageImages[PAGE_PROMOTION].getWidth() >> 1) + 1, offset_y, Graphics.RIGHT | Graphics.BOTTOM);

					if (IGPConfig.useBiggerTouchAreas)
					{
						TOUCH_AREA_W = PageImages[PAGE_PROMOTION].getWidth();
						TOUCH_AREA_H = PageImages[PAGE_PROMOTION].getHeight();
						TOUCH_AREA_X = IGP_HALF_SCRW - 1 - TOUCH_AREA_W / 2;
						TOUCH_AREA_Y = offset_y - PageImages[PAGE_PROMOTION].getHeight();// - TOUCH_AREA_H / 2;
					}

					//Draw Promo text
					if (draw_text)
					{
						drawColor = WHITE_FONT;
						int free_space = (softkeys_y - offset_y);
						int align = Graphics.HCENTER | Graphics.TOP;
						int promoTextBgXOffset = 6; // Equal to a screen width percentage (4 is 4% of screen width)
						int promoTextBgYOffset = 2; // Equal to a screen height percentage
						int promoTextColorBg = 0x000000;
						if(fontHeight*2 > free_space)
						{
							offset_y += fontHeight/3;
						}
						else 
						{
							offset_y += (free_space - fontHeight * numberOfLinesPromoText) / 2;
						}
						// Promo text background
						_gBB.setColor(promoTextColorBg);
						FillRoundRect(_gBB,IGP_HALF_SCRW - longestLinePromoText/2, 
								  	  offset_y - IGP_SCRH * promoTextBgYOffset / 100, 
								  	  longestLinePromoText, 
								  	  numberOfLinesPromoText * fontHeight + IGP_SCRH * promoTextBgYOffset * 2 / 100,
								  	  BLACK);
						drawString(s_textPt, _gBB, IGP_SCRW, IGP_HALF_SCRW, offset_y, align);
					}
				#endif 
				
				}
				else if (currentPage == PAGE_OPERATOR)
				{
				#ifdef USE_NEW_RESOURCE_IGP //xuan
				if (IGPConfig.loadType == LOAD_RESOURCES_ON_PAGE_CHANGE)
					DrawImage(_gBB, PageImages[0], 0, 0, 0);
				else
					DrawImage(_gBB, PageImages[PAGE_OPERATOR], 0, 0, 0);					
				#else
					// compute how much vertical space we need for this page
					//boolean draw_text = s_textPt != null && s_textPt.length() > 0;
					//int logo_height = draw_logo ? GlobalImages[IMG_GAMELOFT].getHeight() : 0;

					/*
					   int frame_height = PageImages[CurrentPageID].getHeight();
					   int taken_space = frame_height + (IGP_SCRH - box_y);
					   int free_space = Math.max(0, IGP_SCRH - taken_space);
					   int padding = free_space / 4; // one padding at the
					// top, one between logo
					// and frame, one
					// between frame and
					// text

					padding = Math.max(2, padding); // min spacing
					*/
					// Draw background image
                   	if (canDrawHeader) 
                        {
                            drawHeader(_gBB, false);
                        }
                    int offset_y = (SOFTKEYS_AREA_TOP + HEADER_BOTTOM) / 2;
                    if (useTransformSpriteRegion)
                    {
                        _gBB.drawRegion(PageImages[currentPage],
                                        0, 0, PageImages[currentPage].getWidth(), PageImages[currentPage].getHeight(),
                                        javax.microedition.lcdui.game.Sprite.TRANS_NONE,
                                        IGP_HALF_SCRW, offset_y, Graphics.RIGHT | Graphics.VCENTER);
                        _gBB.drawRegion(PageImages[currentPage],
                                        0, 0, PageImages[currentPage].getWidth(), PageImages[currentPage].getHeight(),
                                        javax.microedition.lcdui.game.Sprite.TRANS_MIRROR,
                                        IGP_HALF_SCRW, offset_y, Graphics.LEFT | Graphics.VCENTER);
                    }
                    else
                    {
                        DrawImage(_gBB, PageImages[currentPage], IGP_HALF_SCRW, offset_y, Graphics.HCENTER | Graphics.VCENTER);
                    }
                                        
					if (IGPConfig.useBiggerTouchAreas)
					{
						//#ifndef ANDROID
						TOUCH_AREA_W = PageImages[currentPage].getWidth();
						TOUCH_AREA_W -= (2 * GlobalImages[IMG_LEFTWHITE].getWidth());
						//#endif
						TOUCH_AREA_H = PageImages[currentPage].getHeight();
						TOUCH_AREA_X = IGP_HALF_SCRW - TOUCH_AREA_W/2;
						TOUCH_AREA_Y = offset_y - TOUCH_AREA_H/2;
					}
					if( completeCatalogText == null)
					{
						bDetermineSizeOnly = true;
						drawString(getString(pageTextId), null, 0, 0, 0, 0);
						offset_y -= s_text_h/2;

						// Draw text
						int oldColor = drawColor;
						drawColor = WHITE_FONT;
						drawString(getString(pageTextId), _gBB, PageImages[currentPage].getWidth() / 2, //Max Width
								IGP_HALF_SCRW, offset_y, Graphics.HCENTER | Graphics.TOP);
						drawColor = oldColor;
					}
					else
					{
						DrawImage(_gBB, completeCatalogText, IGP_HALF_SCRW, IGP_SCRH / 2, Graphics.HCENTER | Graphics.VCENTER);
					}
				#endif
				}
//#ifdef ENABLE_GLIVE
//@				else if (currentPage == PAGE_GLIVE)
//@				{
//@					// compute how much vertical space we need for this page
//@					int logo_height = GLiveImage.getHeight();
//@					int img_heigt = PageImages[PAGE_GLIVE].getHeight();
//@					int offset_y = 0;
//@					bDetermineSizeOnly = true;
//@					drawString(getString(IGP_TEXT_GLIVE_1), _gBB, IGP_SCRW-50, IGP_HALF_SCRW, offset_y, Graphics.HCENTER | Graphics.TOP);
//@					int str1Height = s_text_h; 
//@					bDetermineSizeOnly = true;
//@					drawString(getString(IGP_TEXT_GLIVE_2), _gBB, IGP_SCRW-50, IGP_HALF_SCRW, offset_y, Graphics.HCENTER | Graphics.TOP);
//@					int str2Height = s_text_h;
//@					int freeSpace = IGP_SCRH - logo_height - box_h - str1Height - str2Height - img_heigt;
//@					int padding = freeSpace/6;
//@					boolean tinyScreen = false;
//@					if (padding < 0)
//@					{
//@						padding = 1;
//@						tinyScreen = true;
//@					}
//@
//@					DrawImage(_gBB, GLiveImage, IGP_HALF_SCRW, offset_y , Graphics.HCENTER | Graphics.TOP);
//@
//@					offset_y += logo_height + padding;
//@
//@					drawColor = WHITE_FONT;
//@					drawString(getString(IGP_TEXT_GLIVE_1), _gBB, IGP_SCRW*3/4, IGP_HALF_SCRW, offset_y, Graphics.HCENTER | Graphics.TOP);
//@					offset_y+= str1Height + padding - (tinyScreen?6:0);
//@
//@					DrawImage(_gBB, PageImages[PAGE_GLIVE], IGP_HALF_SCRW-1, offset_y, Graphics.HCENTER | Graphics.TOP);
//@
//@					if (IGPConfig.useBiggerTouchAreas)
//@					{
//@						TOUCH_AREA_W = PageImages[PAGE_GLIVE].getWidth();
//@						TOUCH_AREA_H = PageImages[PAGE_GLIVE].getHeight();
//@						TOUCH_AREA_X = IGP_HALF_SCRW - 1 - TOUCH_AREA_W/2;
//@						TOUCH_AREA_Y = offset_y;
//@					}
//@
//@
//@					drawColor = RED_FONT;
//@					drawString(getString(IGP_TEXT_GLIVE_FREECHAT), _gBB, IGP_SCRW*3/4, IGP_HALF_SCRW, offset_y+(img_heigt/2), Graphics.HCENTER | Graphics.VCENTER);
//@					offset_y+= img_heigt + padding;					
//@
//@					drawColor = WHITE_FONT;
//@					drawString(getString(IGP_TEXT_GLIVE_2), _gBB, IGP_SCRW*3/4, IGP_HALF_SCRW, offset_y-2, Graphics.HCENTER | Graphics.TOP);
//@					offset_y+= str2Height + padding;
//@				}
//#endif					
				else
				{
					// compute text space ( use the s_text_h value )
					bDetermineSizeOnly = true;
					
				#ifdef USE_NEW_RESOURCE_IGP
				if (IGPConfig.loadType == LOAD_RESOURCES_ON_PAGE_CHANGE)
					DrawImage(_gBB, PageImages[0], 0, 0, 0);
				else
					DrawImage(_gBB, PageImages[currentPage], 0, 0, 0);
				#else
					drawString(getString(pageTextId), _gBB, 0, 0, 0 , 0);

					// compute how much vertical space we need for this page
					int splash_height = PageImages[currentPage].getHeight();
					int softkeys_height = 0;
					int text_offset_y = 0;
					boolean treeLinesFix = false;

					int availableWidth = IGP_SCRW - (IGPConfig.rightSoftkeyXOffsetFromBorders + IGPConfig.leftSoftkeyXOffsetFromBorders);
				    availableWidth -= GlobalImages[(IGPConfig.isTouchScreen ? IMG_BUTTON_WHITE : IMG_TICK)].getWidth() * 2;

					// If text doesn't fit between softkeys, place text above softkeys.
					if (s_text_w > availableWidth)
					{
						softkeys_height = GlobalImages[(IGPConfig.isTouchScreen ? IMG_BUTTON_WHITE : IMG_TICK)].getHeight();
					}
					// If softkeys are removed, don't compute softkey height.
					if (IGPConfig.removeSelectSoftkey && IGPConfig.removeBackSoftkey)
					{
						softkeys_height = 0;
					}

					int taken_space = splash_height + s_text_h + softkeys_height;
					int free_space = Math.max(0, IGP_SCRH - taken_space);

                    //Interline extension for specific small resolution add some space to inter line
                    int interLineExtend = IGP_SCRW <= 176?2:0; //This was added in drawString, but we shoul find a cleanest way.

					if( (s_text_h > (fontHeight*2)+interLineExtend) && ( (free_space/3) > (fontHeight+interLineExtend)/2 ) )//In case there are 3 lines but is enough space, consider as if would be 2 lines
					{
						taken_space = splash_height + fontHeight*2 + softkeys_height;
						free_space = Math.max(0, IGP_SCRH - taken_space);
						treeLinesFix = true;
					}
					//#if BLACKBERRY || BLACKBERRY_MIDP
//@					if (centerBB_sk_img != null && softkeys_height == 0)
//@					{
//@						free_space -= centerBB_sk_img.getHeight() + IGP_SK_Y_offset;
//@					}
					//#endif
					int padding = free_space / 3; // 1/3 for the top, 1/3 between splash and text

					// Recalculate space
					if (softkeys_height > 0)
					{
						int screen_h_without_softkeys = IGP_SCRH-(IGPConfig.isTouchScreen?GlobalImages[IMG_BUTTON_WHITE].getHeight():GlobalImages[IMG_TICK].getHeight());
						taken_space = splash_height + s_text_h;
						free_space = Math.max(0, IGP_SCRH - taken_space);
						if( (s_text_h > (fontHeight*2)+interLineExtend) && ( (free_space/3) > s_text_h-(fontHeight+interLineExtend)*2 ) )//In case there are 3 lines but is enough space, consider as if would be 2 lines
						{
							taken_space = splash_height + fontHeight*2;
							free_space = Math.max(0, IGP_SCRH - taken_space);
							treeLinesFix = true;
						}
						//#if BLACKBERRY || BLACKBERRY_MIDP
//@						if (centerBB_sk_img != null && softkeys_height == 0)
//@						{
//@							free_space -= centerBB_sk_img.getHeight() + IGP_SK_Y_offset;
//@						}
						//#endif
						int temp_padding = free_space/3;
						int used_h = 2*temp_padding + s_text_h + PageImages[currentPage].getHeight();
						if (used_h < screen_h_without_softkeys)
						{
							padding = temp_padding;
						}
					}

					// draw the page
					int offset_y = padding;
					int splash_y = offset_y;

					DrawImage(_gBB, PageImages[currentPage], IGP_HALF_SCRW, splash_y, Graphics.HCENTER | Graphics.TOP);

					if (IGPConfig.useBiggerTouchAreas)
					{
						TOUCH_AREA_W = PageImages[currentPage].getWidth();
						//#ifndef ANDROID
						TOUCH_AREA_W -= (2 * GlobalImages[IMG_LEFTWHITE].getWidth());
						//#endif
						TOUCH_AREA_H = PageImages[currentPage].getHeight();
						TOUCH_AREA_X = IGP_HALF_SCRW - TOUCH_AREA_W / 2;
						TOUCH_AREA_Y = splash_y;
					}

					offset_y += PageImages[currentPage].getHeight();
					offset_y = (offset_y + softkeys_y - s_text_h)/2; 

					if (optionalFeatures[OPTIONAL_FEATURE_ORANGE_GLOBAL])
					{
						drawColor = WHITE_FONT;
					}
					drawColor = WHITE_FONT;
					drawString(getString(pageTextId), _gBB, 0, IGP_HALF_SCRW, offset_y, Graphics.HCENTER | Graphics.TOP);
				#endif	
				}
			}

			// Draw back/tick softkeys
			// Draw Side Arrows
		#ifdef USE_NEW_RESOURCE_IGP			
			if ( ValidRealPages > 1 )
			{
				// compute the animation offset
				int interval = Math.abs((int) ((System.currentTimeMillis() / ARROW_ANIM_DELAY) % SIDE_ARROWS_ANIMATION_WIDTH) - (SIDE_ARROWS_ANIMATION_WIDTH / 2));
				int imgLeft = GLOBAL_IMAGE_ARROW_LEFT_N;
				int imgRight = GLOBAL_IMAGE_ARROW_RIGHT_N;


				if (_redArrowLeft || (IGPConfig.isTouchScreen && lastTouchActionPressed == ACTION_LEFT))
				{
					imgLeft = GLOBAL_IMAGE_ARROW_LEFT_H;
					_arrowPressedCounter++;
				}
				if (_redArrowRight || (IGPConfig.isTouchScreen && lastTouchActionPressed == ACTION_RIGHT))
				{
					imgRight = GLOBAL_IMAGE_ARROW_RIGHT_H;
					_arrowPressedCounter++;
				}

				int arrowOffsetX = 1 + interval + GlobalImages[imgLeft].getWidth();
				int numberOffsetX = arrowOffsetX - ((GlobalImages[imgLeft].getWidth() * 20) / 100); // Number has a 20% offset
			
				DrawImage(_gBB, GlobalImages[imgLeft], arrowOffsetX, IGP_HALF_SCRH, Graphics.RIGHT | Graphics.VCENTER);				
				DrawImage(_gBB, GlobalImages[imgRight], IGP_SCRW - arrowOffsetX, IGP_HALF_SCRH, Graphics.LEFT | Graphics.VCENTER);				
				if (_arrowPressedCounter > 4)
				{
					_redArrowLeft = false;
					_redArrowRight = false;
					_arrowPressedCounter = 0;
				}
			}
		#else			
			if ( ValidRealPages > 1 )
			{
				// compute the animation offset
				int interval = Math.abs((int) ((System.currentTimeMillis() / ARROW_ANIM_DELAY) % SIDE_ARROWS_ANIMATION_WIDTH) - (SIDE_ARROWS_ANIMATION_WIDTH / 2));
				int imgLeft = IMG_LEFTWHITE;
				int imgRight = IMG_RIGHTWHITE;
				int img4 = IMG_4WHITE;
				int img6 = IMG_6WHITE;

				if (_redArrowLeft || (IGPConfig.isTouchScreen && lastTouchActionPressed == ACTION_LEFT))
				{
					imgLeft = IMG_LEFTBLUE;
					img4 = IMG_4BLUE;
					_arrowPressedCounter++;
				}
				if (_redArrowRight || (IGPConfig.isTouchScreen && lastTouchActionPressed == ACTION_RIGHT))
				{
					imgRight = IMG_RIGHTBLUE;
					img6 = IMG_6BLUE;
					_arrowPressedCounter++;
				}

				int arrowOffsetX = 1 + interval + GlobalImages[imgLeft].getWidth();
				int numberOffsetX = arrowOffsetX - ((GlobalImages[imgLeft].getWidth() * 20) / 100); // Number has a 20% offset

				DrawImage(_gBB, GlobalImages[imgLeft], arrowOffsetX, IGP_HALF_SCRH, Graphics.RIGHT | Graphics.VCENTER);
				if (!(IGPConfig.isTouchScreen || IGPConfig.removeArrowLabels))
				{
					DrawImage(_gBB, GlobalImages[img4], numberOffsetX, IGP_HALF_SCRH, Graphics.RIGHT | Graphics.VCENTER);
				}

				DrawImage(_gBB, GlobalImages[imgRight], IGP_SCRW - arrowOffsetX, IGP_HALF_SCRH, Graphics.LEFT | Graphics.VCENTER);
				if (!(IGPConfig.isTouchScreen || IGPConfig.removeArrowLabels))
				{
					DrawImage(_gBB, GlobalImages[img6], IGP_SCRW - numberOffsetX, IGP_HALF_SCRH, Graphics.LEFT | Graphics.VCENTER);
				}

				if (_arrowPressedCounter > 4)
				{
					_redArrowLeft = false;
					_redArrowRight = false;
					_arrowPressedCounter = 0;
				}
			}
		#endif
		}

		private static void drawList(Graphics _gBB)
		{
			// Draw Gameloft logo
		#ifdef USE_NEW_RESOURCE_IGP //xuan
			if (IGPConfig.loadType == IGP.LOAD_RESOURCES_ON_PAGE_CHANGE)
			{
				if (m_IsInstall_First)
					DrawImage(_gBB, PageImages[0], 0, 0, 0);
				else
				{
					_gBB.setClip(0,0,IGP_SCRW,16);
					DrawImage(_gBB, PageImages[0], 0, 0, 0);
					_gBB.setClip(0,IGP_SCRH - 12,IGP_SCRW,12);
					DrawImage(_gBB, PageImages[0], 0, 0, 0);
					if(currentPage == PAGE_WN)
					{
						_gBB.setClip(0,45,IGP_SCRW,20);
						DrawImage(_gBB, PageImages[0], 0, 27, 0);
						_gBB.setClip(0,65,IGP_SCRW,20);
						DrawImage(_gBB, PageImages[0], 0, 8, 0);
					}
					if(currentPage == PAGE_BS)
					{
						_gBB.setClip(0,45,IGP_SCRW,20);
						DrawImage(_gBB, PageImages[0], 0, 8, 0);
						_gBB.setClip(0,65,IGP_SCRW,20);
						DrawImage(_gBB, PageImages[0], 0, -12, 0);
					}
					_gBB.setClip(0,0,IGP_SCRW,IGP_SCRH);
				}
			}
			else
			{
				DrawImage(_gBB, PageImages[currentPage], 0, 0, 0);
			}
			listSelector_w = IGP_SCRW - IGPConfig.DRAW_LIST_OFFSET_W;
			listSelector_x = (IGP_SCRW >> 1) - (listSelector_w >> 1);
			int list_y = GAMELOFT_LOGO_Y + IGPConfig.DRAW_LIST_OFFSET_Y_START;//s_text_h;
			//if (!m_IsInstall_First) list_y = 44;
			
			listSelector_index = s_igpListDisplayItemStart;				
			int item_vspace = IGPConfig.DRAW_LIST_HEIGHT;//xuan
			listSelector_y = list_y + (item_vspace * listSelector_index);
			listSelector_h = item_vspace;
		#else
			
			int offsetY = GAMELOFT_LOGO_Y;
			
			// Items List background customization
			int listBackgroundXOffset = 2; // Horizontal space between background and list items (e.g.: 3 = 3% of screen width) 
			int listBackgroundYOffset = 2;
			int listBackgroundColor = 0x000000;

			if (canDrawHeader)
			{
				drawHeader(_gBB, true);
				offsetY += PageImages[currentPage].getHeight();
			}

			int softkeyHeight = (IGPConfig.isTouchScreen ? GlobalImages[IMG_BUTTON_BLUE].getHeight() : GlobalImages[IMG_BACK].getHeight());
			softkeyHeight += softkeysAdditionalSpace;

			// Calculate view port area (between header and softkeys)
			int viewPortX = 0;
			int viewPortY = offsetY + GAMELOFT_LOGO_Y;
			int viewPortWidth = IGP_SCRW;
			int viewPortHeight = IGP_SCRH - viewPortY - softkeyHeight; // - (IGP_SCRH * 2 * listBackgroundYOffset / 100);

			// Calculate item height.
			int iconHeight = LIST_Images[currentList][ValidLISTIdx[currentList][0]].getHeight();
			int itemTextMaxLines = determineAmountOfLines(currentList);
			int itemHeight = Math.max(itemTextMaxLines * fontHeight, iconHeight + (iconHeight / 4));
			// Calculate arrow height.
			int arrowHeight = GlobalImages[IMG_LEFTWHITE].getWidth();
			// Calculate how many items can be displayed.
			int maxItems = viewPortHeight / itemHeight;
			// Calculate list background height.
			int backgroundHeight = (itemHeight * currentList_nbItems + (IGP_SCRH * 2 * listBackgroundYOffset / 100));

			// Determine if we need to scroll.
			if (backgroundHeight > viewPortHeight)
			{
			    // Take in account arrows.
			    viewPortY += (arrowHeight +  (arrowHeight >> 1));
			    viewPortHeight -= (2 * (arrowHeight +  (arrowHeight >> 1)));

			    // Recalculate visible items.
			    maxItems = viewPortHeight / itemHeight;
			}

//#ifdef DEBUG
			// Draw view port rectangle.
			_gBB.setColor(0xFF00FF);
			_gBB.drawRect(viewPortX, viewPortY, viewPortWidth, viewPortHeight);
//#endif

			// Set list y start position (if there are less items center the list).
			int listY = viewPortY;
			int visibleItems = maxItems;
			if (currentList_nbItems < maxItems)
			{
			    visibleItems = currentList_nbItems;
			}
			listY = viewPortY + ((viewPortHeight >> 1) - ((visibleItems * itemHeight) >> 1));

	        // Re-calculate list background height according to visible items.
            backgroundHeight = (itemHeight * visibleItems + (IGP_SCRH * 2 * listBackgroundYOffset / 100));

			// Update global variable for update (retrocompa).
			LIST_visibleItemCount = visibleItems;

			int y = listY;
			if (maxItems < currentList_nbItems)
			{
                int arrowColor = LIST_ARROW_SCROLL_COLOR;
				if (s_ItemListIndex > 0)
				{
                    if(lastTouchActionPressed == ACTION_UP)
                    {
                        arrowColor = LIST_ARROW_BORDER_COLOR;
                    }
					drawArrow(_gBB, IGP_HALF_SCRW, y - arrowHeight, arrowHeight, arrowColor, true, false);
					Y_littleArrow_Up = y - arrowHeight;
				}

                arrowColor = LIST_ARROW_SCROLL_COLOR;
				if (s_ItemListIndex != currentList_nbItems - 1)
				{
                    if (lastTouchActionPressed == ACTION_DOWN)
                    {
                        arrowColor = LIST_ARROW_BORDER_COLOR;
                    }
					drawArrow(_gBB, IGP_HALF_SCRW, y + (LIST_visibleItemCount * itemHeight) + arrowHeight, arrowHeight, arrowColor, true, true);
					Y_littleArrow_Down = y + (LIST_visibleItemCount * itemHeight);
				}
			}

			y = listY + (itemHeight / 2) + 1;

			// Compute layout
			listSelector_w = IGP_SCRW - (2 * GlobalImages[IMG_LEFTWHITE].getWidth() + GlobalImages[IMG_LEFTWHITE].getWidth() / 4 + SIDE_ARROWS_ANIMATION_WIDTH);
            if (IGP_SCRW > IGP_SCRH)
            {
                listSelector_w -= GlobalImages[IMG_LEFTWHITE].getWidth()/2;
            }
			listSelector_x = (IGP_SCRW >> 1) - (listSelector_w >> 1);
			int list_space_arround_icon = (IGP_SCRW * 2) / 100; // 2%
			int list_item_x = listSelector_x + list_space_arround_icon;
			int list_text_x = list_item_x + LIST_Images[currentList][0].getWidth() + list_space_arround_icon;
                        int list_text_width = listSelector_w - list_space_arround_icon - LIST_Images[currentList][0].getWidth();

			// Draw items list background
			_gBB.setColor(listBackgroundColor);
			FillRoundRect(_gBB, listSelector_x - IGP_SCRW * listBackgroundXOffset/100, 
					      listY - IGP_SCRH * listBackgroundXOffset/100, 
					      listSelector_w + IGP_SCRW * 2 * listBackgroundYOffset/100, 
					      backgroundHeight, 
						  BLACK);

			// draw item list
			for (int i = s_igpListDisplayItemStart; i < (s_igpListDisplayItemStart + visibleItems); i++)
			{
				int item = ValidLISTIdx[currentList][i];

				// draw item's icon
				DrawImage(_gBB, LIST_Images[currentList][item], list_item_x, y, Graphics.VCENTER | Graphics.LEFT);

				// draw item's title
				drawColor = BLUE_GRAY_FONT;
				if ((currentList == COMPLETE_CATALOG_LIST) || (item == (LIST_Texts[currentList].length - 1)))
				{
					drawColor = RED_FONT;
				}
				drawString(getString(LIST_Texts[currentList][item]), _gBB, list_text_width, list_text_x, y, Graphics.VCENTER | Graphics.LEFT, MAX_NB_LINES_ON_WN_BS);
				y += itemHeight;
			}

			// Draw item selection
			listSelector_index = s_ItemListIndex - s_igpListDisplayItemStart;		

			listSelector_y = listY + (itemHeight * listSelector_index);
			listSelector_h = itemHeight;

			// For TouchScreen Devices		
			Y_TouchPosition = listY;
		#endif //USE_NEW_RESOURCE_IGP
			// Draw selector
			_gBB.setColor(LIST_SELECTION_COLOR);
			_gBB.drawRoundRect(listSelector_x, listSelector_y, 
							   listSelector_w, listSelector_h, 
							   IGP_SCRW * 5/100, IGP_SCRH * 5/100);
		}
		
		private static int determineAmountOfLines(int list)
		{
			int linesMax = 0;
			for(int i=0;i<LIST_Texts[currentList].length;i++)
			{
				int lines = 1;
				int searchIndex=0;
				while(getString(LIST_Texts[list][i]).indexOf("\n",searchIndex) != -1)
				{
					searchIndex = getString(LIST_Texts[list][i]).indexOf("\n",searchIndex) + 1;
					lines ++;
				}
				if(lines>linesMax)
				{
					linesMax = lines;
				}
			}	
			return Math.max(2,linesMax);
		}
		
		
		private static void drawHeader(Graphics _gBB, boolean drawTitle)
		{
			int logoOffsetX = IGP_SCRW * 3/100; 
			int logoOffsetY = GAMELOFT_LOGO_Y; 
			int headerHeight = logoOffsetY + GlobalImages[IMG_GAMELOFT].getHeight() + GAMELOFT_LOGO_Y; 
			int headerWidth = IGP_SCRW;
			int numberOfBaseLines = (IGP_SCRH >= 400) ? 2 : 1;

			DrawImage(_gBB, GlobalImages[IMG_GAMELOFT], logoOffsetX, logoOffsetY, Graphics.TOP | Graphics.LEFT);
			if(drawTitle)
			{
				drawColor = WHITE_FONT;
				drawString(pageTextId, _gBB, headerWidth, headerHeight - GAMELOFT_LOGO_Y + 3, Graphics.BOTTOM | Graphics.RIGHT);
			}
			// Separation line(s)
			_gBB.setColor(HEADER_SEPARATION_LINE_COLOR);
                        int i = 0;
			for (; i < numberOfBaseLines; i++)
			{
				_gBB.drawLine(0, headerHeight + i, headerWidth, headerHeight + i);
			}
                        HEADER_BOTTOM = headerHeight + i;
		}
		
		private static void drawArrow(Graphics g, int x, int y, int height, int fillColor, boolean rotate, boolean flip)
		{
			int iFlip = flip ? (-1) : 1;

			if ((height % 2) == 0)
			{
				height--;
			}

			g.setColor(LIST_ARROW_BORDER_COLOR);

			if (rotate)
			{
				FillTriangle(g, x, y, x - (height >> 1), y + (iFlip * (height >> 1)), x + (height >> 1), y + (iFlip * (height >> 1)));
			}
			else
			{
				FillTriangle(g, x, y, x - (iFlip * (height >> 1)), y - (height >> 1), x - (iFlip * (height >> 1)), y + (height >> 1));
			}

			g.setColor(fillColor);

			if (rotate)
			{
				FillTriangle(g, x, y + iFlip, x - (height >> 1) + 2, (y + (iFlip * (height >> 1))) - iFlip, (x + (height >> 1)) - 2, (y + (iFlip * (height >> 1))) - iFlip);
			}
			else
			{
				FillTriangle(g, x - iFlip, y, x - (iFlip * (height >> 1)) + iFlip, y - (height >> 1) + 2, x - (iFlip * (height >> 1)) + iFlip, (y + (height >> 1)) - 2);
			}
		}

		private static void getFlashingBoxRect()
		{
			bDetermineSizeOnly = true;
		#if !REDUCE_CODE_IGP //xuan
			drawString(iButtonStringId, null, 0, 0, Graphics.VCENTER | Graphics.HCENTER);

			int box_x_offset = getFontMetric(' ', FONT_W) / 2;
			int box_y_offset = getFontMetric(' ', FONT_H) / 3;

			box_w = s_text_w + box_x_offset;
			box_h = s_text_h + box_y_offset;

			// adjust 1 pixel
			if (((s_text_h + box_h) % 2) != 0)
			{
				box_h++;
			}
			
		#endif
			box_x = IGP_HALF_SCRW - (box_w / 2);

			if (pagesType[currentPage] == IGP_TYPE_PROMOS)
			{
				box_y = PRESS5_TO_GETIT_Y - (box_h / 2);
			}
			else if (currentPage == PAGE_PROMOTION
					|| currentPage == PAGE_OPERATOR
//#ifdef ENABLE_GLIVE
//@					//                || currentPage == PAGE_GLIVE
//#endif
					|| currentPage == PAGE_ORANGE
				)
			{
				box_y = IGP_SCRH - GlobalImages[(IGPConfig.isTouchScreen ? IMG_BUTTON_WHITE : IMG_TICK)].getHeight()
				        - softkeysAdditionalSpace - box_h;
			}

			if (optionalFeatures[OPTIONAL_FEATURE_ORANGE_GLOBAL] && currentPage == PAGE_ORANGE)
			{
				box_y = (IGP_SCRH * 75) / 100; // 75%
			}
		}

		private static void drawFlashingBox(Graphics g)
		{
			int xBoxPadding; // Horizontal space between text and box border.
			if (bDisplayButton)
			{
				g.setColor(BOX_COLOR);
			#ifdef USE_NEW_RESOURCE_IGP
				xBoxPadding = 1;
			#else
				xBoxPadding = roundCorner[0].getWidth()/2;
			#endif	
				box_x -= xBoxPadding;
				box_w += 2*xBoxPadding;
				box_h++;

				if (optionalFeatures[OPTIONAL_FEATURE_ORANGE_GLOBAL])
				{
					g.setColor(0xFF7F00);
				}
				if (IGPConfig.isTouchScreen && !isOKSoftkeyBeenTouched && lastTouchActionPressed == ACTION_SELECT_BOX)            	
				{
				#ifdef USE_NEW_RESOURCE_IGP
					DrawImage(g,GlobalImages[GLOBAL_IMAGE_VISIT_V],box_x,box_y,Graphics.HCENTER|Graphics.VCENTER);
				#else
					g.setColor(0xDE9C00);
					FillRoundRect(g, box_x - 2, box_y - 2, box_w + 4, box_h + 4, LIGHT_BROWN);
					g.setColor(0x5800F2);
					FillRoundRect(g, box_x, box_y, box_w, box_h, PURPLE);
				#endif
				}
				else
				{
				#ifdef USE_NEW_RESOURCE_IGP
					if (box_y > IGP_SCRH/2)
						DrawImage(g,GlobalImages[GLOBAL_IMAGE_VISIT],box_x,box_y,Graphics.HCENTER|Graphics.VCENTER);
					else
						DrawImage(g,GlobalImages[GLOBAL_IMAGE_VISIT_V],box_x,box_y,Graphics.HCENTER|Graphics.VCENTER);
				#else
					FillRoundRect(g, box_x, box_y, box_w, box_h, DARK_RED);
				#endif
				}
			#ifndef USE_NEW_RESOURCE_IGP
                drawColor = WHITE_FONT;
				drawString(iButtonStringId, g, IGP_HALF_SCRW, box_y + (box_h >> 1), Graphics.VCENTER | Graphics.HCENTER);
			#endif
			}
		}

		private static void drawSoftkeys(Graphics g)
		{
		#ifdef USE_NEW_RESOURCE_IGP
			if(true) return;
		#else
			if (useCommandBar)
		    {
                        return;
                    }
                    int softkey_drawPosition_Y = IGP_SCRH - IGP_SK_Y_offset;
                    int softkeyLeft_drawPosition_X = IGP_LSK_X;
                    int softkeyRight_drawPosition_X = IGP_SCRW - IGP_RSK_X;
                    int backgroundXPadding = 0; // Horizontal space between softkeys and background border
                    int backgroundYPadding = 0; // Vertical space between softkeys and background border
                    
                    boolean isMinScreenHeight = IGP_SCRH >= 295; // Tests for the minimun phone screen height that can use softkeys background
                    boolean isSoftkeyRemoved = IGPConfig.removeBackSoftkey || IGPConfig.removeSelectSoftkey;
//#if BLACKBERRY || BLACKBERRY_MIDP
//@                 boolean isBackgroundVisible = isMinScreenHeight && (!isSoftkeyRemoved || centerBB_sk_img != null);
//#else
                    boolean isBackgroundVisible = isMinScreenHeight && !isSoftkeyRemoved;
//#endif

                    int softkeyHeight;
//#if BLACKBERRY || BLACKBERRY_MIDP
//@                 if (centerBB_sk_img != null && centerBB_sk_img.getHeight() > GlobalImages[IMG_TICK].getHeight())
//@                 {
//@                    softkeyHeight = centerBB_sk_img.getHeight();
//@                 }
//@                 else
//@                 {
//@                    softkeyHeight = GlobalImages[IMG_TICK].getHeight();
//@                 }
//#else
                    softkeyHeight = GlobalImages[IMG_TICK].getHeight();
//#endif
                    SOFTKEYS_AREA_TOP = softkey_drawPosition_Y - softkeyHeight;

                    // Draw softkeys background
                    if (isBackgroundVisible)
		    {
                        if (IGPConfig.isTouchScreen)
                        {
                            softkeyHeight = GlobalImages[IMG_BUTTON_WHITE].getHeight();
                        }
                        backgroundXPadding = 2;
                        backgroundYPadding = 4;
                        softkey_drawPosition_Y -= 4;
                        softkeyLeft_drawPosition_X += 4;
                        softkeyRight_drawPosition_X -= 4;
                        g.setColor(0x000000);
                        FillRoundRect(g, backgroundXPadding,
                                      softkey_drawPosition_Y - softkeyHeight - backgroundYPadding,
                                      IGP_SCRW - 2 * backgroundXPadding,
                                      softkeyHeight + 2 * backgroundYPadding, 
                                      BLACK);
                        SOFTKEYS_AREA_TOP -= backgroundYPadding;
                    }
                    softkeysAdditionalSpace = IGP_SCRH - softkey_drawPosition_Y + backgroundYPadding ;

			// Touchscreen softkeys have individual backgrounds
			if (IGPConfig.isTouchScreen)
			{
				Image imgSoftkeyBackgroundLeft = GlobalImages[IMG_BUTTON_WHITE];
				Image imgSoftkeyBackgroundRight = GlobalImages[IMG_BUTTON_WHITE];


				if (lastTouchActionPressed == ACTION_BACK)        		
				{
					if (IGPConfig.softkeyOKOnLeft)
					{
						imgSoftkeyBackgroundRight = GlobalImages[IMG_BUTTON_BLUE];
					}
					else
					{
						imgSoftkeyBackgroundLeft = GlobalImages[IMG_BUTTON_BLUE];        			
					}        		
				}
				else if (lastTouchActionPressed == ACTION_SELECT)
				{
					// Only change the color of the OK softkey when ACTION_SELECT cames from the OK softkey
					if (isOKSoftkeyBeenTouched)
					{
						if (IGPConfig.softkeyOKOnLeft)
						{
							imgSoftkeyBackgroundLeft = GlobalImages[IMG_BUTTON_BLUE];        			
						}
						else
						{
							imgSoftkeyBackgroundRight = GlobalImages[IMG_BUTTON_BLUE];
						}        		
					}
				}

				// Config Softkey Backgrounds 
				if (!IGPConfig.removeSelectSoftkey)
				{
					s_displaySoftKeysBackgrounds |= (IGPConfig.softkeyOKOnLeft ? DISPLAY_LEFT_BACKGROUND_SK : DISPLAY_RIGHT_BACKGROUND_SK);
					s_displaySoftKeysIcons |= DISPLAY_SELECT_SK ;
				}
				if (!IGPConfig.removeBackSoftkey)
				{
					s_displaySoftKeysBackgrounds |= (IGPConfig.softkeyOKOnLeft ? DISPLAY_RIGHT_BACKGROUND_SK : DISPLAY_LEFT_BACKGROUND_SK);
					s_displaySoftKeysIcons |= DISPLAY_BACK_SK ;
				}

				// Draw Softkey Backgrounds
				if ((s_displaySoftKeysBackgrounds & DISPLAY_LEFT_BACKGROUND_SK) != 0)
				{
					DrawImage(g, imgSoftkeyBackgroundLeft, softkeyLeft_drawPosition_X, softkey_drawPosition_Y, Graphics.LEFT | Graphics.BOTTOM);
				}
				if ((s_displaySoftKeysBackgrounds & DISPLAY_RIGHT_BACKGROUND_SK) != 0)
				{
					DrawImage(g, imgSoftkeyBackgroundRight, softkeyRight_drawPosition_X, softkey_drawPosition_Y, Graphics.RIGHT | Graphics.BOTTOM);                
				}                

				softkeyLeft_drawPosition_X += (GlobalImages[IMG_BUTTON_WHITE].getWidth() / 2 - GlobalImages[IMG_TICK].getWidth() / 2);
				softkeyRight_drawPosition_X += (- GlobalImages[IMG_BUTTON_WHITE].getWidth() / 2 + GlobalImages[IMG_BACK].getWidth() / 2); 
				softkey_drawPosition_Y += (- GlobalImages[IMG_BUTTON_WHITE].getHeight() / 2 + GlobalImages[IMG_TICK].getHeight() / 2);
			}

			// Config Softkey Icons 
			if (!IGPConfig.removeSelectSoftkey)
			{
				s_displaySoftKeysIcons |= DISPLAY_SELECT_SK ;
			}
			if (!IGPConfig.removeBackSoftkey)
			{
				s_displaySoftKeysIcons |= DISPLAY_BACK_SK ;
			}
			//#if BLACKBERRY || BLACKBERRY_MIDP
//@			if (centerBB_sk_img != null)
//@			{
//@				DrawImage(g, centerBB_sk_img, IGP_SCRW >> 1, softkey_drawPosition_Y, Graphics.HCENTER | Graphics.BOTTOM);
//@			}
			//#endif
			if (IGPConfig.softkeyOKOnLeft)
			{
				if ((s_displaySoftKeysIcons & DISPLAY_SELECT_SK) != 0)

				{
					//#if BLACKBERRY || BLACKBERRY_MIDP
//@					if (leftBB_sk_img != null)
//@					{
//@						DrawImage(g, leftBB_sk_img, softkeyLeft_drawPosition_X, softkey_drawPosition_Y, Graphics.LEFT | Graphics.BOTTOM);
//@						DrawImage(g, GlobalImages[IMG_TICK], softkeyLeft_drawPosition_X + leftBB_sk_img.getWidth() + 4, softkey_drawPosition_Y, Graphics.LEFT | Graphics.BOTTOM);
//@					}
//@					else
						//#endif
						DrawImage(g, GlobalImages[IMG_TICK], softkeyLeft_drawPosition_X, softkey_drawPosition_Y, Graphics.LEFT | Graphics.BOTTOM);
				}

				if ((s_displaySoftKeysIcons & DISPLAY_BACK_SK) != 0)

				{
					//#if BLACKBERRY || BLACKBERRY_MIDP
//@					if (rightBB_sk_img != null)
//@					{
//@						DrawImage(g, rightBB_sk_img, softkeyRight_drawPosition_X, softkey_drawPosition_Y, Graphics.RIGHT | Graphics.BOTTOM);
//@						DrawImage(g, GlobalImages[IMG_BACK], softkeyRight_drawPosition_X - rightBB_sk_img.getWidth() - 4, softkey_drawPosition_Y, Graphics.RIGHT | Graphics.BOTTOM);
//@					}
//@					else
						//#endif
						DrawImage(g, GlobalImages[IMG_BACK], softkeyRight_drawPosition_X, softkey_drawPosition_Y, Graphics.RIGHT | Graphics.BOTTOM);
				}
			}
			else
			{
				if ((s_displaySoftKeysIcons & DISPLAY_BACK_SK) != 0)
				{
					DrawImage(g, GlobalImages[IMG_BACK], softkeyLeft_drawPosition_X, softkey_drawPosition_Y, Graphics.LEFT | Graphics.BOTTOM);
				}

				if ((s_displaySoftKeysIcons & DISPLAY_SELECT_SK) != 0)
				{
					DrawImage(g, GlobalImages[IMG_TICK], softkeyRight_drawPosition_X, softkey_drawPosition_Y, Graphics.RIGHT | Graphics.BOTTOM);
				}
			}   
		#endif
		}

		//#ifndef BLACKBERRY	
		private static void setCommandBar(boolean showSelect, boolean showBack)
		{
			if (cmdBack != null)
			{
				GameInstance.removeCommand(cmdBack);
			}

			if (cmdSelect != null)
			{
				GameInstance.removeCommand(cmdSelect);
			}

			if (IGPConfig.softkeyOKOnLeft) // this is needed for proper emulation in GLEmu. Should not make a difference in the device.
			{
				if (showSelect)
				{
					GameInstance.addCommand(cmdSelect);
				}

				if (showBack)
				{
					GameInstance.addCommand(cmdBack);
				}
			}
			else
			{
				if (showBack)
				{
					GameInstance.addCommand(cmdBack);
				}

				if (showSelect)
				{
					GameInstance.addCommand(cmdSelect);
				}
			}
		}

		private static void removeCommands()
		{
			if (cmdBack != null)
			{
				GameInstance.removeCommand(cmdBack);
				cmdBack = null;
			}
			if (cmdSelect != null)
			{
				GameInstance.removeCommand(cmdSelect);
				cmdSelect = null;
			}
		}
		//#endif	

		// Draws a  gradient C1 >> C2 >> C1, clipped by
		// the region described by x, y, wx, wy.
		// First, set the above variable to the RGB of both colors.
		private static void drawGradient(Graphics g, int x, int y, int width, int height, int topColor, int bottomColor, boolean useSandwichGradient)
		{
		#ifndef USE_NEW_RESOURCE_IGP
			// Make sure the gradient isn't outside the screen
			if ((y + height) > IGP_SCRH)
			{
				height = IGP_SCRH - y;
			}
			if ((x + width) > IGP_SCRW)
			{
				width = IGP_SCRW - x;
			}

			if (IGPConfig.useDetailedBackgrounds)
			{
				// Descompose colors
				int color1R = topColor >> 16;
				int color1G = (topColor >> 8) & 0x0000FF;
				int color1B = topColor & 0x0000FF;

				int color2R = bottomColor >> 16;
				int color2G = (bottomColor >> 8) & 0x0000FF;
				int color2B = bottomColor & 0x0000FF;

				int differenceR = color2R - color1R;
				int differenceG = color2G - color1G;
				int differenceB = color2B - color1B;


				int intr = color1R;
				int intg = color1G;
				int intb = color1B;

				int stp = 0;

				for (int i = y; i < (y + height); i++)
				{
					if (useSandwichGradient)
					{                   
						if (i < (height / 2))
						{
							stp = i;
						}
						else if (i == (height / 2))
						{
							intr = color2R;
							intg = color2G;
							intb = color2B;
							stp = 0;
						}
						else
						{
							stp = (height / 2) - i;
						}

						g.setColor(intr + ((differenceR * stp) / (IGP_SCRH / 2)), intg + ((differenceG * stp) / (IGP_SCRH / 2)), intb + ((differenceB * stp) / (IGP_SCRH / 2)));
					}
					else
					{                   
						int colorR = color1R + (((i - y) * differenceR) / height);
						int colorG = color1G + (((i - y) * differenceG) / height);
						int colorB = color1B + (((i - y) * differenceB) / height);

						g.setColor(colorR, colorG, colorB);
					}
					g.drawLine(x, i, x + width, i);
				}
			}
			else
			{
				g.setColor(bottomColor);
				g.fillRect(x, y, width, height);
			}
		#endif
		}

		private static Image createFont(byte[] buffer, int offset, int length, int paletteIndex, int finalColor)
		{
			int pngPlteOffset = 0;
			int tmpIndex = offset;
			long c;

			while ((pngPlteOffset == 0) && (tmpIndex < (offset + length)))
			{
				if (((buffer[tmpIndex    ] & 0xFF) == (80 & 0xFF)) &&  //P 
						((buffer[tmpIndex + 1] & 0xFF) == (76 & 0xFF)) &&  //L
						((buffer[tmpIndex + 2] & 0xFF) == (84 & 0xFF)) &&  //T
						((buffer[tmpIndex + 3] & 0xFF) == (69 & 0xFF))   ) //e
				{
					pngPlteOffset = tmpIndex;
				}

				tmpIndex++;
			}

			int plteBytesSize = ((buffer[pngPlteOffset - 4] << 24) & 0xFF000000) + ((buffer[pngPlteOffset - 3] << 16) & 0x00FF0000) + ((buffer[pngPlteOffset - 2] << 8) & 0x0000FF00) + ((buffer[pngPlteOffset - 1] << 0) & 0x000000FF);

			/*
			// font outline
			buffer[pngPlteOffset + 4 + (3 * paletteIndex)    ] = (byte) ((finalColor & 0xFF0000 ) >> 16);
			buffer[pngPlteOffset + 4 + (3 * paletteIndex) + 1] = (byte) ((finalColor & 0x00FF00 ) >> 8 );
			buffer[pngPlteOffset + 4 + (3 * paletteIndex) + 2] = (byte) ((finalColor & 0x0000FF )      );

			// font fill
			if ( finalColor == 0x00FFFFFE )
			{
			buffer[pngPlteOffset + 7 + (3 * paletteIndex)    ] = (byte) (( finalColor /*& 0xFF0000*//*) >> 16);
			buffer[pngPlteOffset + 7 + (3 * paletteIndex) + 1] = (byte) (( finalColor /*& 0x00FF00*//*) >> 8 );
			buffer[pngPlteOffset + 7 + (3 * paletteIndex) + 2] = (byte) (( finalColor /*& 0x0000FE*//*)      );
			}
		*/

			// font only color (fill)
			buffer[pngPlteOffset + 4 + (3 * paletteIndex)    ] = (byte) ((finalColor & 0xFF0000 ) >> 16);
		buffer[pngPlteOffset + 4 + (3 * paletteIndex) + 1] = (byte) ((finalColor & 0x00FF00 ) >> 8 );
		buffer[pngPlteOffset + 4 + (3 * paletteIndex) + 2] = (byte) ((finalColor & 0x0000FF )      );


		byte[] tmpBuf = new byte[plteBytesSize + 4];
		System.arraycopy(buffer, pngPlteOffset, tmpBuf, 0, plteBytesSize + 4);

		long[] crc_table = new long[256];

		for (int n = 0; n < 256; n++)
		{
			c = n;

			for (int k = 0; k < 8; k++)
			{
				if ((c & 1) == 1)
				{
					c = 0xedb88320L ^ (c >> 1);
				}else
				{
					c = c >> 1;
				}
			}

			crc_table[n] = c;
		}

		// Return the CRC of the bytes buf[0..len-1]. 
		c = 0xffffffffL;

		for (int n = 0; n < tmpBuf.length; n++)
		{
			c = crc_table[(int) (c ^ tmpBuf[n]) & 0xff] ^ (c >> 8);
		}

		c ^= 0xffffffffL;

		buffer[pngPlteOffset + 4 + plteBytesSize] = (byte) ((c & 0xFF000000) >> 24);
		buffer[pngPlteOffset + 4 + plteBytesSize + 1] = (byte) ((c & 0x00FF0000) >> 16);
		buffer[pngPlteOffset + 4 + plteBytesSize + 2] = (byte) ((c & 0x0000FF00) >> 8);
		buffer[pngPlteOffset + 4 + plteBytesSize + 3] = (byte) ((c & 0x000000FF) >> 0);
		tmpBuf = null;
		crc_table = null;
		System.gc();

		return CreateImage(buffer, offset, length);
		}

		// Draws a progressbar horizontally centered, at y, with total width wx
		private static void drawProgressBar(Graphics g, int y, int wx, int progress, int total)
		{
			if (progress > total)
			{
				progress = total;
			}

			int x = (IGP_SCRW - wx) / 2;
			g.setClip(x,0,wx+2/*1 pixel for each border */,IGP_SCRH);//3462109 - IGP: Loading: Loading bar spills out of its border.
			g.setColor(PROGRESSBAR_BORDER_COLOR);
			g.drawRect(x, y, wx, PROGRESSBAR_BORDER_HEIGHT);

			int fillWidth = (((wx - (2 * PROGRESSBAR_BORDER_SPACING) - (2 * 1)) * progress) / total) + 1;
			g.setColor(PROGRESSBAR_FILL_COLOR);
			g.fillRect(x + 1 + PROGRESSBAR_BORDER_SPACING, y + 1 + PROGRESSBAR_BORDER_SPACING, fillWidth, PROGRESSBAR_FILL_HEIGHT);
		}

		private static void FillTriangle(Graphics g, int x1, int y1, int x2, int y2, int x3, int y3)
		{
			//#if SPRINT || MIDP1
//@			g.drawLine(x1, y1, x2, y2);
//@			g.drawLine(x1, y1, x3, y3);
//@			g.drawLine(x2, y2, x3, y3);
			//#else
			g.fillTriangle(x1, y1, x2, y2, x3, y3);
			//#endif
		}

		private static void DrawRegion(Graphics g, Image img, int xsrc, int ysrc, int width, int height, int xdest, int ydest)
		{
			//#if SPRINT || MIDP1
//@			SetClip(g, xdest, ydest, width, height);
//@			DrawImage(g, img, xdest - xsrc, ydest - ysrc, Graphics.TOP | Graphics.LEFT);
			//#else
			//#if BLACKBERRY_MIDP
//@			SetClip(g, xdest, ydest, width, height);
//@			DrawImage(g, img, xdest - xsrc, ydest - ysrc, Graphics.TOP | Graphics.LEFT);
			//#else
			g.drawRegion(img, xsrc, ysrc, width, height, 0, xdest, ydest, Graphics.TOP | Graphics.LEFT);
			//#endif
			//#endif
		}

		private static void DrawImage(Graphics g, Image img, int x, int y, int anchor)
		{
			if (IGPConfig.useAnchorPoints)
			{
				g.drawImage(img, x, y, anchor);
			}
			else
			{
				if ((anchor & Graphics.HCENTER) != 0)
					x -= img.getWidth() >> 1;
				if ((anchor & Graphics.VCENTER) != 0)
					y -= img.getHeight() >> 1;
				if ((anchor & Graphics.RIGHT) != 0)
					x -= img.getWidth();
				if ((anchor & Graphics.BOTTOM) != 0)
					y -= img.getHeight();

				g.drawImage(img, x, y, 0);
			}
		}

    private static void FillRoundRect(Graphics g, int x, int y, int width, int height, int cornersColor)
    {
	#ifndef USE_NEW_RESOURCE_IGP
        //#if SPRINT || MIDP1
    	//@ g.fillRect(x, y, width, height);
    	//#else
    	Image corner = roundCorner[cornersColor];
        g.fillRect(x + corner.getWidth(), y, width - 2 * corner.getWidth(), height);

        // Middle rectangle
        g.fillRect(x, y + corner.getHeight(),
                   width, height - 2 * corner.getHeight() + 1);

        // Corners
        // Top left
        g.drawRegion(corner, 0, 0, corner.getWidth(), corner.getHeight(),
                     javax.microedition.lcdui.game.Sprite.TRANS_NONE,
                     x, y, Graphics.TOP | Graphics.LEFT);        	
        // Top right
        g.drawRegion(corner, 0, 0, corner.getWidth(), corner.getHeight(),
                     javax.microedition.lcdui.game.Sprite.TRANS_ROT90,
                     x + width - corner.getWidth(), y,
                     Graphics.TOP | Graphics.LEFT);

        // Bottom right
        g.drawRegion(corner, 0, 0, corner.getWidth(), corner.getHeight(),
                     javax.microedition.lcdui.game.Sprite.TRANS_ROT180,
                     x + width - corner.getWidth(), y + height,
                     Graphics.BOTTOM | Graphics.LEFT);

        // Bottom left
        g.drawRegion(corner, 0, 0, corner.getWidth(), corner.getHeight(),
                     javax.microedition.lcdui.game.Sprite.TRANS_ROT270,
                     x, y + height,
                     Graphics.BOTTOM | Graphics.LEFT);
    	//#endif
	#endif
    }

		private static void SetClip(Graphics g, int x, int y, int w, int h)
		{
			x = Math.max(x, 0);
			y = Math.max(y, 0);
			w = Math.min(w, IGP_SCRW);
			h = Math.min(h, IGP_SCRH);
			g.setClip(x, y, w, h);
		}

		// internal method to handle platformRequest on its own thread
		public void run()
		{
            log("platformRequest Thread alive!");
            log("Thread.activeCount() = " + Thread.activeCount());
			if (IGPConfig.platformRequestType == PLATFORM_REQUEST_ON_NEW_THREAD)
			{
				while (IGPRunning)
				{
					try
					{
						if (IGPServerURL != null)
						{
							URLPlatformRequest = IGPServerURL;
							doPlatformRequest();
							IGPServerURL = null;

                            // Stop thread for ZVIP.
                            if (enterZVIP)
                            {
                                IGPRunning = false;
                            }
						}

						Thread.sleep(1000);
					}
					//#ifdef BLACKBERRY		
//@					catch (Throwable e)
						//#else			
						catch (Exception e)
						//#endif
						{
							//#ifdef DEBUG
//@							e.printStackTrace();
							//#else
							e = null;
							//#endif

						}
				}
			}
            log("platformRequest Thread dead!");
		}

		private static void MarkIGPsAsVisited()
		{
			wasVisited = true;
			wasVisitedCached = true;

			RecordStore igpNew = null;

			try
			{
				igpNew = RecordStore.openRecordStore(IGP_RECORDSTORE_NAME, false);
			}
			//#ifdef BLACKBERRY		
//@			catch (Throwable d)
				//#else			
				catch (Exception d)
				//#endif
				{
					try
					{
						igpNew = RecordStore.openRecordStore(IGP_RECORDSTORE_NAME, true);
					}
					//#ifdef BLACKBERRY		
//@					catch (Throwable e)
						//#else			
						catch (Exception e)
						//#endif
						{
							//#ifdef DEBUG
//@							e.printStackTrace();
							//#else
							e = null;
							//#endif

						}
					d = null;
				}

			try
			{
				if (igpNew != null)
				{
					igpNew.closeRecordStore();
				}
			}
			//#ifdef BLACKBERRY		
//@			catch (Throwable e)
				//#else			
				catch (Exception e)
				//#endif
				{
					//#ifdef DEBUG
//@					e.printStackTrace();
					//#else
					e = null;
					//#endif
				}
		}

		// returns if the user entered the IGPs at least once
		// it is used to know whether to draw the "NEW" sign on the menu or not
		// have in mind that you have to activate m_bHandleRecordstore to use it
		private static boolean wasVisited()
		{
			if (IGP.isAvailable)
			{
				if (wasVisitedCached)
				{
					return wasVisited;
				}

				try
				{
					RecordStore igpNew = null;
					igpNew = RecordStore.openRecordStore(IGP_RECORDSTORE_NAME, false);
					igpNew.closeRecordStore();
					wasVisited = true;
				}
				//#ifdef BLACKBERRY		
//@				catch (Throwable e)
					//#else			
					catch (Exception e)
					//#endif
					{
						e = null;
					}

				wasVisitedCached = true;
			}

			return wasVisited;
		}

		// handles platformRequest
		private static void doPlatformRequest()
		{
			if ((URLPlatformRequest != null) && (URLPlatformRequest.length() > 0))
			{
				String request = URLPlatformRequest;

				// Set to null after calling platformRequest
				if (!IGPConfig.letGameCodeCallBrowser)
				{
					URLPlatformRequest = null;
				}

				log("urlPlatformRequest = " + request);

				try
				{
					//#ifdef SPRINT
//@					if (IGPConfig.useStandardPlatformRequest)
//@					{
//@						PlatformRequest(request);
//@					}
//@					else
//@					{
//@						if (!IGPConfig.letGameCodeCallBrowser)
//@						{
//@							com.sprintpcs.util.System.setExitURI(request);
//@						}
//@					}
					//#else        
					PlatformRequest(request);
					//#endif

					if (IGPConfig.useLongSleepAfterPlatformRequest)
					{
						Thread.sleep(2000);
					}
					else{
						Thread.sleep(200);
					}
				}
//#ifdef BLACKBERRY		
//@				catch (Throwable e)
//#else
			    catch (Exception e)
//#endif
				{
//#ifdef DEBUG
//@               e.printStackTrace();
//#else
     			  e = null;
//#endif
                }

				// Change state after platformRequest
				if (IGPConfig.exitIGPAfterPlatformRequest)
				{
					currentState = STATE_EXIT_IGP;
				}
				else
				{
					if (!enterZVIP)
					{
						currentState = STATE_PAGE;
					}
					else
					{
						currentState = STATE_EXIT_IGP;
					}
				}
				

				if (IGPConfig.destroyApplicationAfterPlatformRequest)
				{
					//#ifndef BLACKBERRY
					MidletInstance.notifyDestroyed();
					//#endif
					//TODO: look how to exit app in BB
				}
			}
		}

		public static boolean drawNewArrow(Graphics g, Image arrow, int x, int y, int anchor)
		{
			//#ifdef DEBUG
//@			if ((arrow == null) || (g == null))
//@			{
//@				throw new RuntimeException("IGP::drawNewArrow received a null parameter.");
//@			}
			//#endif

			if (wasVisited())
			{
				return false;
			}

			if ( !isAvailable || (arrow == null) || (g == null))
			{
				return false;
			}

			if ((System.currentTimeMillis() - lastArrowUpdate) > ARROW_BLINK_TIME)
			{
				drawArrow = !drawArrow;
				lastArrowUpdate = System.currentTimeMillis();
			}

			if (drawArrow)
			{
				DrawImage(g, arrow, x, y, anchor);
				return true;
			}
			return true;
		}

//#ifndef BLACKBERRY	
		public void commandAction(Command item, Displayable display) 
		{
			if (useCommandBar)
			{
				if (item == IGP.cmdSelect) 
				{
					IGP.update(IGP.ACTION_SELECT);
				}
				else if (item == IGP.cmdBack) 
				{
					IGP.update(IGP.ACTION_BACK);
				}
			}
		}
//#endif	

//#ifdef BLACKBERRY	
//@		private static final String getAppProperty(String s)
//@		{
//@			if (IGPConfig.useHardcodedLinks)
//@			{
//@				String str = (String)s_jadTable.get(s);
//@				if (str != null && str != "") {
//@					return str;
//@				}
//@				return null;
//@			}
//#ifdef BLACKBERRY_OLD
//@			return null;
//#else
//@			else
//@			{
//@				try 
//@				{
//@					net.rim.device.api.system.CodeModuleGroup[] allGroups = 
//@						net.rim.device.api.system.CodeModuleGroupManager.loadAll();
//@					net.rim.device.api.system.CodeModuleGroup myGroup = null;
//@					String moduleName = ApplicationDescriptor.currentApplicationDescriptor().getModuleName();
//@
//@					for (int i = 0; i < allGroups.length; i++) {
//@						if (allGroups[i].containsModule(moduleName)) {
//@							myGroup = allGroups[i];
//@							break;
//@						}
//@					}
//@
//@					//Check: we have not found a jad file
//@					if (myGroup == null)
//@					{
//@						s_jadNotFound = true;
//@						return null;
//@					}
//@					// Get the property
//@					String header = myGroup.getProperty(s);
//@					return header;
//@				} 
//@				catch (Throwable e)
//@				{
//#ifdef DEBUG
//@					e.printStackTrace();
//@					s_jadNotFound = true;
//#else
//@					e = null;
//@					s_jadNotFound = true;
//#endif
//@				}
//@				return null;
//@			}
//#endif
//@		}
//#else
		private static final String getAppProperty(String s)
		{
			if(IGPConfig.useHardcodedLinks)
			{
				String str = (String)s_jadTable.get(s);
				if (str != null && str != "") {
					return str;
				}
				return null;
			}
			else
			{
				return MidletInstance.getAppProperty(s);        
			}
		}
//#endif

//#if BLACKBERRY || BLACKBERRY_MIDP
//@		private static final void PlatformRequest(String s)
//@		{
//#ifdef BLACKBERRY_MIDP
//@			if (IGPConfig.useStandardPlatformRequest)
//@			{
//@				try {
//@					MidletInstance.platformRequest(s);
//@				} catch (Throwable t) {
//#ifdef DEBUG
//@					t.printStackTrace();
//#endif
//@				}
//@			}
//@			else
//#endif	
//@			{
//@				BrowserSession browser = createWAPBrowserSession();
//@				if (browser == null)
//@					browser = Browser.getDefaultSession();
//@				browser.displayPage(s);
//@			}
//@		}
//#else
		private static final void PlatformRequest(String s) throws javax.microedition.io.ConnectionNotFoundException
		{
		    MidletInstance.platformRequest(s);
		}
//#endif	

		public static void setSoftKeyBackground(Image imgBackground, boolean blink)
		{
			if (imgBackground != null)
			{
				if (blink)
				{
					sk_background_blink_img = imgBackground;
				}
				else
				{
					sk_background_img = imgBackground;
				}
			}
		}

		public static void setSoftKeyIcons(Image left, Image right)
		{
			if (right != null && left != null)
			{
				left_sk_img = left;
				right_sk_img = right;
			}
		}

		//#if BLACKBERRY || BLACKBERRY_MIDP 
//@		public static void setBBSoftKeyIcons(Image left, Image right, Image center)
//@		{
//@			if (right != null)
//@				rightBB_sk_img = right;
//@			if (left != null)
//@				leftBB_sk_img = left;
//@			if (center != null)
//@				centerBB_sk_img = center;
//@		}
//@
//@		public static void setBBSoftKeyIcons(Image left, Image right)
//@		{
//@			if (right != null)
//@				rightBB_sk_img = right;
//@			if (left != null)
//@				leftBB_sk_img = left;		
//@		}
		//#endif

		public static void setArrowIcon(Image left, Image right, boolean blink)
		{
			if (right != null && left != null)
			{
				if (blink)
				{
					arrow_left_blink_img = left;
					arrow_right_blink_img = right;
				}
				else
				{
					arrow_left_img = left;
					arrow_right_img = right;
				}
			}
		}
		
		/**
		 * Overrides the 4 and 6 images on side arrows
		 * Don't use removeArrowLabels flag!
		 */
		public static void setArrowTexts(Image left,Image leftPressed,Image right, Image rightPressed)
		{
		    arrow_custom_left_img = left;
	        arrow_custom_right_img = right;
	        arrow_custom_left_blink_img = leftPressed;
	        arrow_custom_right_blink_img = rightPressed;
		}

		public static void setGameloftLogo(Image imgLogo)
		{
			if (imgLogo != null)
			{
				imgGameloftLogo = imgLogo;
			}               
		}

		public static void setSplashImage(Image imgSplash, int pageIndex)
		{
			if (imgSplash == null)
			{
				log("setSplashImage method received a null image as parameter!");
				return;
			}
			if (pageIndex < 0 || pageIndex >= NumberOfPromos)
			{
				log("setSplashImage method received an incorrect pageIndex: " + pageIndex + ".\nPossible values are: ");
				for (int i = 0; i < NumberOfPromos; i++)
				{
					log(i + " ");
				}
				log(".\n");
				return;
			}

			promosSplashesOverriden[pageIndex] = imgSplash;
		}

		//#ifdef DEBUG
//@		private static void debugIGPConfig()
//@		{
//@			log("IGPConfig: ");
//@			currentDebugSpacing = IGP_DEBUG_TAB_SPACING;
//@			log("removeSelectSoftkey: " + IGPConfig.removeSelectSoftkey);
//@			log("removeBackSoftkey: " + IGPConfig.removeBackSoftkey);
//@			log("softkeyOKOnLeft: " + IGPConfig.softkeyOKOnLeft);
//@			log("rightSoftkeyXOffsetFromBorders: " + IGPConfig.rightSoftkeyXOffsetFromBorders);
//@			log("leftSoftkeyXOffsetFromBorders: " + IGPConfig.leftSoftkeyXOffsetFromBorders);
//@			log("removeArrowLabels: " + IGPConfig.removeArrowLabels);
//@			log("loadType: " + (IGPConfig.loadType == IGP.LOAD_RESOURCES_ON_START ? "IGP.LOAD_RESOURCES_ON_START" : "IGP.LOAD_RESOURCES_ON_PAGE_CHANGE"));
//@			log("useRedFonts: " + IGPConfig.useRedFonts);
//@			log("useDetailedBackgrounds: " + IGPConfig.useDetailedBackgrounds);
//@			log("createImageOffsetBug: " + IGPConfig.createImageOffsetBug);
//@			log("useUTF8Encoding: " + IGPConfig.useUTF8Encoding);
//@			log("platformRequestType: " + (IGPConfig.platformRequestType == IGP.PLATFORM_REQUEST_ON_PAINT ? "IGP.PLATFORM_REQUEST_ON_PAINT" : "IGP.PLATFORM_REQUEST_ON_NEW_THREAD"));
//@			log("useLongSleepAfterPlatformRequest: " + IGPConfig.useLongSleepAfterPlatformRequest);
//@			log("destroyApplicationAfterPlatformRequest: " + IGPConfig.destroyApplicationAfterPlatformRequest);
//@			log("exitIGPAfterPlatformRequest: " + IGPConfig.exitIGPAfterPlatformRequest);
//@			log("isTouchScreen: " + IGPConfig.isTouchScreen);
//@			log("useBiggerTouchAreas: " + IGPConfig.useBiggerTouchAreas);
//@			log("usePressOKInsteadOfPress5: " + IGPConfig.usePressOKInsteadOfPress5);
//@			log("useAnchorPoints: " + IGPConfig.useAnchorPoints);
//@			log("useAlphaBanners: " + IGPConfig.useAlphaBanners);
//@			log("useStandardPlatformRequest: " + IGPConfig.useStandardPlatformRequest);
//@			log("useHardcodedLinks: " + IGPConfig.useHardcodedLinks);
//@
//@			if (IGPConfig.useHardcodedLinks)
//@			{
//@				currentDebugSpacing = 0;
//@				log("Harcoded IGP Links: ");
//@				currentDebugSpacing = IGP_DEBUG_TAB_SPACING;
//@				log("URL_TEMPLATE_GAME_JAD: " + IGPConfig.URL_TEMPLATE_GAME_JAD);
//@				log("URL_OPERATOR_JAD: " + IGPConfig.URL_OPERATOR_JAD);
//@				log("URL_PT_JAD: " + IGPConfig.URL_PT_JAD);
//@				log("IGP_PROMOS_JAD: " + IGPConfig.IGP_PROMOS_JAD);
//@				log("IGP_WN_JAD: " + IGPConfig.IGP_WN_JAD);
//@				log("IGP_BS_JAD: " + IGPConfig.IGP_BS_JAD);
//@				log("IGP_CATEGORIES_JAD: " + IGPConfig.IGP_CATEGORIES_JAD);
//@				log("IGP_VERSION_JAD: " + IGPConfig.IGP_VERSION_JAD);
//@				log("URL_ORANGE_JAD: " + IGPConfig.URL_ORANGE_JAD);
//@				log("URL_GLIVE_JAD: " + IGPConfig.URL_GLIVE_JAD);
//@			}
//@			currentDebugSpacing = 0;
//@		}
		//#endif

		private static void log(String message)
		{
			log(message, currentDebugSpacing);
		}

		private static void log(String message, int spacing)
		{
//#ifdef DEBUG
//@			String strSpacing = "";
//@			for (int i = 0; i < spacing; i++)
//@			{
//@				strSpacing += " ";
//@			}
//@			System.out.println(IGP_DEBUG_HEADER + strSpacing + message);
//#else        
			return;
//#endif        
		}

		//#if BLACKBERRY || BLACKBERRY_MIDP
//@
//@		/*
//@		 * WAP: This forces the browser to go through the carrier WAP gateway
//@		 *      to access the Internet. Only use the WAP Browser if...
//@		 *     - You need to identify the carrier network that the user is coming
//@		 *       from (the WAP gateway will add this info to the header)
//@		 *     - You want to access a site where the user will be able to stream
//@		 *       content using the RTSP protocol
//@		 *
//@		 * @returns WAP BrowserSession
//@		 */
//@		public static BrowserSession createWAPBrowserSession()
//@		{
//@			// If there are browser services found - search for the WAP2
//@			// Service Record
//@			String uid = null;
//@			int numRecords = records.length;
//@			for( int i = 0; i < numRecords; i++ )
//@			{
//@				ServiceRecord myRecord = records[i];
//@				ServiceRecordHelper myRecordHelper =
//@					new ServiceRecordHelper(myRecord);
//@				if( myRecord.isValid() && !myRecord.isDisabled() &&
//@						(myRecordHelper.getConfigType() ==
//@						 ServiceRecordHelper.SERVICE_RECORD_CONFIG_TYPE_WAP2) &&
//@						(myRecordHelper.getNavigationType() != -1))
//@				{
//@					uid = myRecord.getUid();
//@					break;
//@				}
//@			}
//@			// If there was no WAP2 Service Record found – search for the
//@			// WAP Record
//@			if (uid == null)
//@			{
//@				for( int i = 0; i < numRecords; i++ )
//@				{
//@					ServiceRecord myRecord = records[i];
//@					ServiceRecordHelper myRecordHelper =
//@						new ServiceRecordHelper(myRecord);
//@					if( myRecord.isValid() &&
//@							!myRecord.isDisabled() &&
//@							(myRecordHelper.getConfigType() ==
//@							 ServiceRecordHelper.SERVICE_RECORD_CONFIG_TYPE_WAP) &&
//@							(myRecordHelper.getNavigationType() != -1))
//@					{
//@						uid = myRecord.getUid();
//@						break;
//@					}
//@				}
//@			}
//@			return null == uid ? null : Browser.getSession(uid);
//@		}
//@
//@		/**
//@		 * ServiceRecordHelper is a utility class for parsing the data in the service books 
//@		 */  
//@		public static class ServiceRecordHelper
//@		{
//@			private static final int SERVICE_RECORD_NAVIGATION_TYPE = 7;
//@			private static final int SERVICE_RECORD_CONFIG_TYPE = 12;
//@
//@			public static final int SERVICE_RECORD_CONFIG_TYPE_WAP  = 0;
//@			public static final int SERVICE_RECORD_CONFIG_TYPE_BES  = 1;
//@			public static final int SERVICE_RECORD_CONFIG_TYPE_WIFI = 2;
//@			public static final int SERVICE_RECORD_CONFIG_TYPE_BIS  = 4;
//@			public static final int SERVICE_RECORD_CONFIG_TYPE_WAP2 = 7;
//@
//@			public static final String SERVICE_RECORD_NAME_UNITE = "Unite";
//@
//@			private ServiceRecord record = null;
//@
//@			/**
//@			 * Constructs the ServiceRecordHelper with a ServiceRecord object
//@			 * @param record ServiceRecord
//@			 */
//@			public ServiceRecordHelper(ServiceRecord record)
//@			{
//@				this.record = record;
//@			}
//@
//@			/**
//@			 * This method will return the config type of the service record.
//@			 * This information is contained in the service records application data (12th field)
//@			 *
//@			 * 
//@			 * @return  configType
//@			 */
//@			public int getConfigType()
//@			{
//@				return parseInt(getDataBuffer(SERVICE_RECORD_CONFIG_TYPE));
//@			}
//@
//@
//@			/**
//@			 * This method will return the navigation type of the service record.  
//@			 * This information is contained in the service records application data (7th field).
//@			 * The navigation type indicates whether or not the user can navigate to other 
//@			 * web pages from within the browser instance.
//@			 * 
//@			 * @return  navigationType
//@			 */
//@			public int getNavigationType()
//@			{
//@				return parseInt(getDataBuffer(SERVICE_RECORD_NAVIGATION_TYPE));
//@			}
//@
//@			/**
//@			 * Converts the data buffer for the given type to an int value 
//@			 * to the given type 
//@			 * 
//@			 * @param DataBuffer
//@			 * @return data buffer as an int or -1 if the data buffer is null or can't be read
//@			 */
//@			private int parseInt(DataBuffer buffer)
//@			{
//@				if (buffer != null)
//@				{
//@					try 
//@					{
//@						return ConverterUtilities.readInt(buffer);
//@					} 
//@					catch (EOFException e) 
//@					{
//@						return -1;
//@					}
//@				}
//@
//@				return -1;
//@			}
//@
//@			/**
//@			 * Returns a data buffer from the ServiceRecord based on the given type  
//@			 * 
//@			 * @param type data type
//@			 * @return Corresponding data buffer
//@			 */
//@			private DataBuffer getDataBuffer(int type) 
//@			{
//@				DataBuffer buffer = null;
//@				byte[] data = record.getApplicationData();
//@
//@				if (data != null) 
//@				{
//@					buffer = new DataBuffer(data, 0, data.length, true);
//@
//@					try 
//@					{
//@						buffer.readByte();
//@					} 
//@					catch (EOFException e1) 
//@					{
//@						buffer = null;
//@					}
//@
//@					if (!ConverterUtilities.findType(buffer, type)) 
//@					{
//@						buffer = null;
//@					}
//@				}
//@
//@				return buffer;
//@			}
//@		}
		//#endif

		// Used for hardcoding IGP settings
		private static final void initJadTable()
		{
			s_jadTable.put("URL-TEMPLATE-GAME", IGPConfig.URL_TEMPLATE_GAME_JAD);
			s_jadTable.put("URL-OPERATOR", IGPConfig.URL_OPERATOR_JAD);
			s_jadTable.put("URL-PT", IGPConfig.URL_PT_JAD);
			s_jadTable.put("IGP-PROMOS", IGPConfig.IGP_PROMOS_JAD);
			s_jadTable.put("IGP-WN", IGPConfig.IGP_WN_JAD);
			s_jadTable.put("IGP-BS", IGPConfig.IGP_BS_JAD);
			s_jadTable.put("IGP-CATEGORIES", IGPConfig.IGP_CATEGORIES_JAD);
			s_jadTable.put("IGP-VERSION", IGPConfig.IGP_VERSION_JAD);
			s_jadTable.put("URL-ORANGE", IGPConfig.URL_ORANGE_JAD);
			//#ifdef ENABLE_GLIVE
//@			s_jadTable.put("URL-GLIVE", IGPConfig.URL_GLIVE_JAD);
			//#endif
		}

		public static void setBackGroundImage(Image img)
		{
			if (img == null)
			{
				//#ifdef DEBUG
//@				log("setBackGroundImage is null");
				//#endif
				return;
			}
			backGroundImage = img;
		}

		private static InputStream getResourceAsStreamIGP(String data)
		{
			//#ifdef ANDROID
//@			return  com.gameloft.android.wrapper.Utils.getResourceAsStream(data);
			//#elif BLACKBERRY || BLACKBERRY_MIDP
//@			if(IGPConfig.useBBcompression)
//@			{
//@				return new GZIPInputStream("a".getClass().getResourceAsStream(data));
//@			}   
//@			else
//@			{
//@				return "a".getClass().getResourceAsStream(data);
//@			}
			//#else
			return "a".getClass().getResourceAsStream(data);
			//#endif
		}

        private static short[] _sizes = new short[50];

        /**
        * Split texts in lines according to the initialized screen
        * This must be loaded once text and images are loaded in order to calculate the max width for the lists
        **/
        private static void fixStringPack()
        {
		#ifdef USE_NEW_RESOURCE_IGP
			return;
		#else
            int list_text_width = IGP_SCRW;
            int listSelector_w = IGP_SCRW - (2 * GlobalImages[IMG_LEFTWHITE].getWidth() + GlobalImages[IMG_LEFTWHITE].getWidth() / 4 + SIDE_ARROWS_ANIMATION_WIDTH);
			if (IGP_SCRW > IGP_SCRH)
			{
				listSelector_w -= GlobalImages[IMG_LEFTWHITE].getWidth()/2;
			}
			int listSelector_x = (IGP_SCRW >> 1) - (listSelector_w >> 1);
			int list_space_arround_icon = (IGP_SCRW * 2) / 100; // 2%
			int list_item_x = listSelector_x + list_space_arround_icon;
			int list_text_x = list_item_x + GlobalImages[IMG_MORE].getWidth() + list_space_arround_icon;
                        list_text_width = listSelector_w - 2*list_space_arround_icon - GlobalImages[IMG_MORE].getWidth();

            try
            {
                for (int i = 0; i < _StrMgr_Pack.length; i++)
                {
					//Skip
					if (_StrMgr_Pack[i] != null && i == IGP_TEXT_CATALOG_ORANGE - 1)
					{
						continue;
					}
                    // Filter the range of texts (Visit texts can be displayed in a sinble line the others have '\n')
                    if (_StrMgr_Pack[i] != null &&
                            (i == IGP_TEXT_PRESS_5_TO_VISIT ||
                                    i == IGP_TEXT_PRESS_OK_TO_VISIT ||
                                    i == IGP_TEXT_TOUCHVISIT ||
                                    i == IGP_TEXT_VISIT_YOUR_PORTAL ||
                                    i < IGP_TEXT_CATALOG_ORANGE ||
                                    (i >= IGP_TEXT_WN_GAME_0
                                            && i < IGP_TEXT_BS_GAME_0+BSList.length )))
                    {
                        int maxWidth = IGP_SCRW - 4;//just a small tolerance
                        int maxLines = (i == IGP_TEXT_VISIT_YOUR_PORTAL ? 4 : 3);
                        if( (WNList.length > 1 && i >= IGP_TEXT_WN_GAME_0 && i < IGP_TEXT_WN_GAME_0+WNList.length) ||                              
                            (BSList.length > 1 && i >= IGP_TEXT_BS_GAME_0 && i < IGP_TEXT_BS_GAME_0+BSList.length) ||
                            i == IGP_TEXT_VISIT_YOUR_PORTAL )
                        {
                            maxWidth = list_text_width;
                        }
                        else if(i == IGP_TEXT_PRESS_5_TO_VISIT || i == IGP_TEXT_PRESS_OK_TO_VISIT || i == IGP_TEXT_TOUCHVISIT)
                        {
                            maxLines = 2;
                            maxWidth = IGP_SCRW - (IGPConfig.isTouchScreen ? GlobalImages[IMG_BUTTON_WHITE].getWidth() * 2 : GlobalImages[IMG_TICK].getWidth() * 2);
                            //maxWidth -= (IGPConfig.rightSoftkeyXOffsetFromBorders + IGPConfig.leftSoftkeyXOffsetFromBorders);
                        }
                        else if( i == IGP_TEXT_CATALOG_ORANGE - 1)
                        {
                            maxWidth = (PageImages[PAGE_OPERATOR] != null) ? PageImages[PAGE_OPERATOR].getWidth() >> 1 : IGP_SCRW;
                        }
//#if BLACKBERRY || BLACKBERRY_MIDP
//@							else if( i >0 && i < IGP_TEXT_CATALOG_ORANGE - 4)
//@                         {
//@								if (leftBB_sk_img!= null) maxWidth -= leftBB_sk_img.getWidth();
//@								if (rightBB_sk_img!= null) maxWidth -= rightBB_sk_img.getWidth();
//@                         }
//#endif
//#ifdef DEBUG
//@                            log("_StrMgr_Pack["+i+"] maxWidth=" + maxWidth + " maxLines=" + maxLines + " String->" + _StrMgr_Pack[i]);
//#endif
                            _StrMgr_Pack[i] = SplitStringInLines(_StrMgr_Pack[i], maxWidth, maxLines, true);
                            _StrMgr_Pack[i] = removePipes(_StrMgr_Pack[i]);
                        }
//#ifdef DEBUG
//@                     else if ( _StrMgr_Pack[i] != null )
//@                     {
//@                         log("Skipped _StrMgr_Pack["+i+"] String->" + _StrMgr_Pack[i]);
//@                     }
//#endif
                }
            }
            catch (Exception e) 
            {
//#ifdef DEBUG
//@                e.printStackTrace();
//#endif
            }    
		#endif //USE_NEW_RESOURCE_IGP
        }

        /**
        *   Split lines according the given width
		*	str			- the text to wrap
		*	text_w		- the max width in pixels
		*	maxNbLines	- 
		*	pipes_wrap	-
        **/
        private static String SplitStringInLines(String str, int text_w , int maxNbLines, boolean pipes_wrap)
    	{
    		String stringToReturn = "";
    
    		int lastEnter = 0;
    		int numberOfLines = 0;
    		if(pipes_wrap)
            {
                //add pipes for special charactes wich can be used to separate lines
                str = addPipes(str,text_w);
            }
    		short[] ret = mesureText(str, text_w, 0, pipes_wrap);
    		
    
    		char[] charArray = str.toCharArray();
    		for (int i = 0; i < ret[0]; i++)
    		{
    			if (lastEnter != 0)
    			{
    				numberOfLines++;
    				stringToReturn = stringToReturn.trim();
    				if( numberOfLines >= maxNbLines )
    					break;
    				else
    					stringToReturn += "\n";
    				
    				
    			}
    			stringToReturn += str.substring(lastEnter, ret[(i << 1) + 1]);
    			lastEnter = ret[(i << 1) + 1];
    			if(str.length() != lastEnter && charArray[ret[(i << 1) + 1]] =='\n')
				{
    				lastEnter++;
				}
    
    		}
    
    		return stringToReturn;
    	}

        /**
        *   Mesure the words size
        **/
        private static short[] mesureText(String s, int width, int height, boolean pipes_wrap)
    	{
    		int str_len = s.length();
    		short lineSize = 0;
    		short cnt = 1;
    		short lastSpacePos = 0;
    		short oneButLast = 0;		
    		
    		boolean bSpaceFound = false;
    		short distFromLastSpacePos = 0;

    		char[] charArray = s.toCharArray();
    		
    		for (int i = 0; i < str_len; i++)
    		{
    		    char c = charArray[i];
    			if (c == ' ')
    			{
    				lineSize += getFontMetric(c, FONT_W);
    				
    				oneButLast = lastSpacePos;
    				lastSpacePos = (short) i;
    				
    				bSpaceFound = true;
    				distFromLastSpacePos = 0;
    				
    				// Check if this space is the one but last char
    				if (lastSpacePos == str_len - 2)
    				{
    				    // Check if the last char doesn't fit
    				    if (lineSize + getFontMetric(charArray[lastSpacePos + 1], FONT_W) > width)
    				    {
    				        lastSpacePos = oneButLast;
    				        _sizes[cnt++] = (short) (lastSpacePos + 1);
    	                    _sizes[cnt++] = (short) (lineSize - distFromLastSpacePos);
    	                    lineSize = 0;
    				    }
    				}
					
					boolean lineSizeLargerThanWidth = (lineSize > width);
    
    				if (lineSizeLargerThanWidth)
    				{
    					bSpaceFound = false;
    					// Make sure there are no spaces counted at the end of the line
    					int pos = lastSpacePos;
    					while (pos >= 0 && charArray[pos] == ' ')
    					{
    						pos--;
    
    						lineSize -= getFontMetric(' ', FONT_W);
    					}
    
    					// Make sure there are no spaces at the beginning of the
    					// next line and count how many chars we have after the last space
    					int charsAfterSpace = 0;
    					while (lastSpacePos < str_len && charArray[lastSpacePos] == ' ')
    					{
    						lastSpacePos++;
    						charsAfterSpace++;
    					}
    					
    					// If there is one char, then wrap with the oneButLast space
    					if (charsAfterSpace == 1 && i == str_len - 2)
    					{
    						lastSpacePos = oneButLast;
    					}
    					else
    					{
    						lastSpacePos--;
    						i = lastSpacePos;
    					}
					}
						
					if(lineSizeLargerThanWidth){
    
    					_sizes[cnt++] = (short) (lastSpacePos + 1);
    					_sizes[cnt++] = (short) (lineSize - distFromLastSpacePos);
    					lineSize = 0;
    				}
    				continue;
    			}
    			else if (c == '|')				
    			{
    				if (!pipes_wrap){
    					break;
    				}
    				lineSize += getFontMetric(c, FONT_W);
    
    				lastSpacePos = (short) i;
    				bSpaceFound = true;
    				distFromLastSpacePos = 0;
    
    				if (lineSize > width)
    				{
    					bSpaceFound = false;
    					// Make sure there are no spaces counted at the end of the
    					// line
    					int pos = lastSpacePos;
    					while (pos >= 0 && charArray[pos] == ' ')
    					{
    						pos--;
    
    						lineSize -= getFontMetric(' ', FONT_W);
    					}
    
    					// Make sure there are no spaces at the beginning of the
    					// next line
    					while (lastSpacePos < str_len && charArray[lastSpacePos] == ' ')
    						lastSpacePos++;
    
    					lastSpacePos--;
    					i = lastSpacePos;
    
    					_sizes[cnt++] = (short) (lastSpacePos + 1);
    					_sizes[cnt++] = (short) (lineSize - distFromLastSpacePos);
    					lineSize = 0;
    				}
    				continue;
    			}
    			else if (c == '\n')
    			{
    				_sizes[cnt++] = (short) i; // line lenght
    				_sizes[cnt++] = lineSize;  // pixels used 
    				lineSize = 0;
    				distFromLastSpacePos = 0;
    				continue;
    			}
                
    
    			int charSize = getFontMetric(c, FONT_W) + DEFAULT_CHAR_SPACING;
    
    			distFromLastSpacePos += charSize;
    			lineSize += charSize;
    
    			// Special case: if there's a single word bigger than the desired
    			// width, use the whole word, even if it's out of the screen
    			if (lineSize > width && bSpaceFound)
    			{
    				bSpaceFound = false;
    				int pos = lastSpacePos;
    				while (pos >= 0 && charArray[pos] == ' ')
    				{
    					pos--;
    
    					lineSize -= getFontMetric(' ', FONT_H);
    				}
    				_sizes[cnt++] = (short) (lastSpacePos + 1);
    				_sizes[cnt++] = (short) (lineSize - distFromLastSpacePos);
    				lineSize = 0;
    				i = lastSpacePos;
    			}
    		}
    		if (lineSize != 0)
    		{
    			_sizes[cnt++] = (short) str_len; // line Lenght
    			_sizes[cnt++] = lineSize; 		 // pixels used
    		}
    
    		_sizes[0] = (short) (cnt / 2); //[0] - 
    		return _sizes;
    	}

        /**
        *   Preapre the string for SplitStringInLines adding pipes were correspond and removing carriage returns
        */
        private static String addPipes(String strIn, int maxWidth)
        {
            String strOut = "";
            
            char[] c = strIn.toCharArray();
            
    		for (int i = 0; i < strIn.length(); i++)
            {
                if (c[i] == 0xFF0C || /*fullwidth comma  , */
                    c[i] == 0xFF01 || /*fullwidth exclamation mark  ! */
                    c[i] == 0xFF1F || /*fullwidth question mark  ? */
                    c[i] == 0xFF1B || /*fullwidth semicolon  ; */
                    c[i] == 0xFF1A )  /*fullwidth colon  : */
                {
                    strOut += c[i];
                    strOut += '|';
                }
                else if ( c[i] == 8)//TODO Temporal fix. This caracter should not exists. Must check string export process
                {
                    strOut += '|';
                }
                else if ( c[i]  == '\n')
                {
                    strOut += ' ';
                }
                else
                {
                    strOut += c[i];
                }
            }
            //Chinese can be splited anywere
            if(AvailableLanguages[CurrentLanguage].equals("ZH-TW") && strOut.indexOf('|') == -1 && strOut.indexOf(' ') == -1 && (strOut.length() > 5) )
            {
                strOut = strOut.substring(0,strOut.length()>>1) + "|" + strOut.substring(strOut.length()>>1,strOut.length());   
            }
            return strOut;
        }
        private static String removePipes(String strIn)
        {
            String strOut = "";
            
            char[] c = strIn.toCharArray();
    		for (int i = 0; i < strIn.length(); i++)
            {
                if (c[i] != '|' )
                {
                    strOut += c[i];
                }
            }
            return strOut;
        }
	}
