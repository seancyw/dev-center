class IGPConfig
{
    // set this to true to remove Select softkey
    public static boolean removeSelectSoftkey = false;
    
    // set this to true to remove Back softkey
    public static boolean removeBackSoftkey = false;
    
    // OK is on left, BACK is on right?
    public static boolean softkeyOKOnLeft = true;

    // this is the distance from the borders to the softkeys. Default value is 4.
    static int softkeyXOffsetFromBorders = 4;//deprecated
    static int leftSoftkeyXOffsetFromBorders = 2;
    static int rightSoftkeyXOffsetFromBorders = 2;
    
    // Removes the numbers labels of the side navigation arrows
    public static boolean removeArrowLabels = false;

    // resource loading method
    // resource loading options
    // LOAD_RESOURCES_ON_START (high mem): When entering IGP, load all and free the dataIGP FILE 
    // LOAD_RESOURCES_ON_PAGE_CHANGE (low mem): when entering IGP, load only first valid page,
    // keep pack open and load resources when changing page
    public static int loadType = IGP.LOAD_RESOURCES_ON_START;

    // set this to false if you are having memory problems with images
    public static boolean useRedFonts = true;

    // set this to false if you are having painting performance problems
    public static boolean useDetailedBackgrounds = true;
	
	// set this to true if you want IGP to support both portait and landscape mode
	public static boolean flipScreenOnWidthChange = false;

	// this are the posibility of rotation that the IGP have. By Default SCREEN_ROTATION_NONE is selected.
	public static final int SCREEN_ROTATION_NONE = 0;
	public static final int SCREEN_ROTATION_90 = 1;
	public static final int SCREEN_ROTATION_180 = 2;
	public static final int SCREEN_ROTATION_270 = 3;
	
	// set this when you want to rotate the screen, if not the default value must be SCREEN_ROTATION_NONE.
	public static int screenRotation = SCREEN_ROTATION_NONE;
    
    // used in case screenRotation is set. Determines the number of drawRegion calls to paint the buffer.
    public static int screenRotBufferW = 1;
    public static int screenRotBufferH = 1;

    // createImage with non-0 offset bug workaround
    public static boolean createImageOffsetBug = false;

    // set this to false if you phone does not support UTF-8 encoding
    public static boolean useUTF8Encoding = true;

    // platformRequest behaviour
    //	The platformRequest method that your device supports:
    //	PLATFORM_REQUEST_ON_PAINT: will be done in paintIGP 
    //	PLATFORM_REQUEST_ON_NEW_THREAD: will be done automatically in a new thread
    public static int platformRequestType = IGP.PLATFORM_REQUEST_ON_NEW_THREAD;

	//Sleeps the current thread 2 sec. after invoking platformRequest
	public static boolean useLongSleepAfterPlatformRequest = false;

    // if true, calls notifyDestroyed() after platformRequest() 
    public static boolean destroyApplicationAfterPlatformRequest = true;

    // if true, the IGP.update() will return true after platformRequest()
    public static boolean exitIGPAfterPlatformRequest = false;

    // only put in TRUE if your phone is touchscreen
    public static boolean isTouchScreen = false;
    
    // Set this to true if you want bigger touch areas for PROMO pages (all the splash)
    public static boolean useBiggerTouchAreas = false;    
    
    // Uses an alternative Press OK instead of normal Press 5 banners
    public static boolean usePressOKInsteadOfPress5 = false;
    
    // Set this to false if your phone does not support anchor points
    public static boolean useAnchorPoints = true;

	//Set this to false if your device has problems with drawRGB
	public static boolean useAlphaBanners = true;
	
	// this is now deprecated, it is here just for retro-compat.
	public static boolean enableBlackBerry = false;
	
	//Set this to true to use standard platformRequest on Sprint and BlackBerry builds
	public static boolean useStandardPlatformRequest = false;

	//Set this to let your game code handle opening browser
	public static boolean letGameCodeCallBrowser = false;
	
	// enable for BlackBerry devices or othes special cases
    public static boolean useHardcodedLinks = false;
	
	// The fields below are used instead the JAD propertyes when useHardcodedLinks is activated  
    public static String URL_TEMPLATE_GAME_JAD = "URL-TEMPLATE-GAME-XXX";
    public static String URL_OPERATOR_JAD = "URL-OPERATOR-XXX";
    public static String URL_PT_JAD = "URL-PT-XXX";
    public static String IGP_PROMOS_JAD = "IGP-PROMOS-XXX";
    public static String IGP_WN_JAD = "IGP-WN-XXX";
    public static String IGP_BS_JAD = "IGP-BS-XXX";
    public static String IGP_CATEGORIES_JAD = "IGP-CATEGORIES-XXX";
    public static String IGP_VERSION_JAD = "IGP-VERSION-XXX";
    public static String URL_ORANGE_JAD = "URL-ORANGE-XXX";
    public static String URL_GLIVE_JAD = "URL-GLIVE-XXX";
    public static String OVI_JAD = "OVI";
    
    // Enable Blackberry GZIP Compression
    public static boolean useBBcompression = false;
	
	public static final int NUMBER_PAGE_IGP = 7;
  public static final int DRAW_LIST_OFFSET_W = 40;
  public static final int DRAW_LIST_OFFSET_Y_START = 59;
  public static final int DRAW_LIST_HEIGHT = 20;
  public static final int FONT_HEIGHT = 8;
}
