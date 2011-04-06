// QTTDemoView.cpp
//

#include "stdafx.h"
#include "Resource.h"
#include "QTTDemoView.h"
#include "SizesDlg.h"

#include <math.h>

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

#pragma warning (disable: 4355)	// 'this' : used in base member initializer list

IMPLEMENT_DYNCREATE(CQTTDemoView, CScrollView)

BEGIN_MESSAGE_MAP(CQTTDemoView, CScrollView)
	ON_WM_LBUTTONDOWN()
	ON_WM_SETCURSOR()
	ON_WM_KEYDOWN()
	ON_WM_KEYUP()
	ON_UPDATE_COMMAND_UI(ID_INDICATOR_TRACKER, OnUpdateIndicatorTracker)
	ON_COMMAND_RANGE(ID_OPTIONS_ROTATE, ID_OPTIONS_PATHDOTTED, OnOptions)
	ON_UPDATE_COMMAND_UI_RANGE(ID_OPTIONS_ROTATE, ID_OPTIONS_PATHDOTTED, OnUpdateOptions)
	ON_COMMAND_RANGE(ID_COLORS_MARKRECTANGLE, ID_COLORS_CENTER, OnOptionsColors)
	ON_COMMAND(ID_OPTIONS_SIZES, OnOptionsSizes)
END_MESSAGE_MAP()

CQTTDemoView::CQTTDemoView()
: m_Tracker(this)
, m_iPath(-1)
{
	// Initialize cursors
	// We override some of QTransformTracker's default cursors.
	// Of course, we must have the cursors available in our resources.
	m_Tracker.LoadCursor(QTransformTracker::CursorCenter, IDC_BULLSEYE);

	UINT ourCursorIDs[] =
	{
		IDC_COPY4WAY, IDC_ROTATE, IDC_SHEAR_HOR, IDC_SHEAR_VERT
	};
	for (int i = 0; i <= sizeof(ourCursorIDs)/sizeof(UINT); i++)
		m_Tracker.LoadCursor(i + 6, ourCursorIDs[i]);

	InitPensAndBrushes();
}

CQTTDemoView::~CQTTDemoView()
{
	for (int i = 0; i < 6; i++)
	{
		delete m_pBrushes[i];
		delete m_pPens[i];
	}
}

void CQTTDemoView::OnDraw(CDC* pDC)
{
	{
		CDC *mdc = new CDC;
		//De lam cai gi vay troi?
		mdc->CreateCompatibleDC(pDC);
		CBitmap* m_bitmap = new CBitmap();
		//Ham de load cai file den cai bitmap // Win32 API
		m_bitmap->m_hObject=(HBITMAP)::LoadImage(
			NULL,
			"tamtit.bmp",
			IMAGE_BITMAP,
			520,
			758,
			LR_LOADFROMFILE);
		//Phai select object thi no moi ve dc.
		mdc->SelectObject(m_bitmap);
		CRect rect;
		//Lay client Rect
		GetClientRect(&rect);
		//BitBlt() // Khong hieu
		//pDC->BitBlt(100,100,400,500,mdc,0,0,SRCCOPY);
		float radians = 3.1416f*45/180;
		float minx = 50;
		float miny = 100;
		float cosine = (float)cos(radians);
		float sine = (float)sin(radians);
		XFORM xform;
		xform.eM11 = cosine;
		xform.eM12 = -sine;
		xform.eM21 = sine;
		xform.eM22 = cosine;
		xform.eDx = (float)-minx;
		xform.eDy = (float)-miny;
		SetWorldTransform(pDC->GetSafeHdc(),&xform);		
		pDC->BitBlt(0,0,400,500,mdc,100,100,SRCCOPY);		
		pDC->TransparentBlt(100,100,400,500,mdc,0,0,400,500,RGB(144,3,225));
		mdc->DeleteDC();		
		CBrush brush;
		brush.CreatePatternBrush(m_bitmap);

		// Block for Graphics
		Graphics g(pDC->GetSafeHdc());
		g.SetSmoothingMode(SmoothingModeHighQuality);
		g.SetInterpolationMode(InterpolationModeHighQualityBicubic);
		g.SetPixelOffsetMode(PixelOffsetModeHighQuality);
		
		for (int i = 0; i < 6; i++)
		{
			//g.RotateTransform(45);
			g.FillPath(m_pBrushes[i], & m_Paths[i]);
			g.DrawPath(m_pPens[i], & m_Paths[i]);
		}
	}
	//Dung de paint may cai nut de co the xoay, resize v.v...
	m_Tracker.Draw(pDC);
}

void CQTTDemoView::OnPrepareDC(CDC* pDC, CPrintInfo* pInfo)
{
	CScrollView::OnPrepareDC(pDC, pInfo);

	CSize sz = pDC->GetWindowExt();
	sz = pDC->GetViewportExt();
	if (pDC->GetMapMode() != MM_TEXT) pDC->SetWindowOrg(0, 500);
}

void CQTTDemoView::OnInitialUpdate()
{
	// Called at application start, and at the File=>Reset command.

	CScrollView::OnInitialUpdate();
	CSize sizeTotal;
	sizeTotal.cx = sizeTotal.cy = 100;

	// QTransformTracker works in all mapping modes. If you want to check,
	// change MM_TEXT to f.i. MM_LOENGLISH. Note: in that case all
	// objects will be mirrored across the horizontal axis.
	SetScrollSizes(MM_TEXT /* MM_LOENGLISH */, sizeTotal);

	m_Tracker.Clear(NULL);

	// Restore QTransformTracker's defaults. No member function for this in
	// QTransformTracker, because I figured it would only make sense in this demo program.
	m_Tracker.m_Options = QTransformTracker::OptionDefault;
	m_Tracker.m_colorMark = RGB(192, 192, 192);
	m_Tracker.m_colorHandles = RGB(0, 0, 0);
	m_Tracker.m_colorCenter = RGB(0, 0, 0);
	m_Tracker.m_colorTrack = RGB(0, 0, 0);
	m_Tracker.m_colorPath = RGB(0, 0, 0);
	m_Tracker.SetMetrics(4, 4, 2);

	InitSillyObjects();
}

void CQTTDemoView::OnLButtonDown(UINT nFlags, CPoint point)
{
	TRACE("11111111111111111111111void CQTTDemoView::OnLButtonDown(UINT nFlags, CPoint point)");
	CClientDC dc(this);
	OnPrepareDC(& dc);
	dc.DPtoLP(& point);

	int r;
	if (m_iPath < 0)	// No object selected
	{
		// If we hit an object...
		m_iPath = HitTest(point);

		// ... load it in QTransformTracker, setting the center
		if (m_iPath >= 0) m_Tracker.Load(m_Paths[m_iPath], true, &dc);
	}

	while (m_iPath >= 0)  // As long as an object is selected...
	{
		// ... start a tracking operation.
		r = m_Tracker.Track(& dc, nFlags, point);
		TRACE1(_T("QTransformTracker::Track() returns %d\n"), r);

		if (r > 0)	// tracking operation succeeded
		{
			// Get the bounding rectangle of the object before transforming.
			Rect rcBefore;
			m_Paths[m_iPath].GetBounds(& rcBefore, NULL, m_pPens[m_iPath]);

			// Get the transformation Matrix
			Matrix * pMat = m_Tracker.GetTransform();

			// Apply the transformation to the object
			m_Paths[m_iPath].Transform(pMat);

			// Not needed anymore
			delete pMat;

			// Reload the now transformed object. Don't set the
			// center point, because we might have tracked the center.
			m_Tracker.Load(m_Paths[m_iPath], false);

			// Get the bounding rectangle after transforming.
			Rect rcChanged, rcAfter;
			m_Paths[m_iPath].GetBounds(& rcAfter, NULL, m_pPens[m_iPath]);

			// Determine which part of the screen is changed
			rcChanged.Union(rcChanged, rcBefore, rcAfter);

			// Convert to CRect
			CRect rcInval(rcChanged.GetLeft(), rcChanged.GetTop(), rcChanged.GetRight(), rcChanged.GetBottom());

			// Invalidate the screen. In a real application, this would be done in a
			// far more refined way.
			InvalidateRect(rcInval);
			return;
		}
		else if (r == QTracker::TrackCancelled) return;
		else
		{
			// ... but perhaps on another one.
			m_iPath = HitTest(point);

			// If not, clear QTransformTracker
			if (m_iPath == -1) m_Tracker.Clear(& dc);

			// Otherwise, load the new object
			else m_Tracker.Load(m_Paths[m_iPath], true, &dc);
		}
	}
}

// Helper for OnLButtonDown
int CQTTDemoView::HitTest(CPoint point)
{
	for (int i = 5; i >= 0; i--)
		if (m_Paths[i].IsVisible(point.x, point.y)) break;
	return i;
}

BOOL CQTTDemoView::OnSetCursor(CWnd* pWnd, UINT nHitTest, UINT message)
{
	CClientDC dc(this);
	OnPrepareDC(& dc);

	BOOL b = FALSE;

	// See if QTransformTracker wants to handle this message
	if (nHitTest == HTCLIENT) b = m_Tracker.OnSetCursor(& dc);
	
	// If not, delegate to base class
	if (!b) b = CScrollView::OnSetCursor(pWnd, nHitTest, message);
	return b;
}

void CQTTDemoView::OnKeyDown(UINT nChar, UINT nRepCnt, UINT nFlags)
{
	// If Control or the space bar is pressed, maybe the cursor needs to be changed.
	if (nChar == VK_CONTROL || nChar == VK_SPACE)
	{
		CClientDC dc(this);
		OnPrepareDC(& dc);
		m_Tracker.OnSetCursor(& dc);
	}

	CScrollView::OnKeyDown(nChar, nRepCnt, nFlags);
}

void CQTTDemoView::OnKeyUp(UINT nChar, UINT nRepCnt, UINT nFlags)
{
	// If Control or the space bar is released, maybe the cursor needs to be changed.
	if (nChar == VK_CONTROL || nChar == VK_SPACE)
	{
		CClientDC dc(this);
		OnPrepareDC(& dc);
		m_Tracker.OnSetCursor(& dc);
	}

	CScrollView::OnKeyUp(nChar, nRepCnt, nFlags);
}

void CQTTDemoView::OnUpdateIndicatorTracker(CCmdUI *pCmdUI)
{
	// Get the indicator string from QTransformTracker.
	// For this to work, the static array indicators needs a change,
	// and we need a string resource IDS_INDICATOR_TRACKER. See MainFrm.cpp.
	// This is the standard MFC way to set an indicator string on the status bar.
	pCmdUI->SetText(m_Tracker.GetIndicatorString());
	pCmdUI->Enable(m_Tracker.IsTracking());
}

void CQTTDemoView::OnOptions(UINT nID)
{
	nID -= ID_OPTIONS_ROTATE;
	UINT option = 0x0001 << nID;

	CClientDC dc(this);
	OnPrepareDC(& dc);
	m_Tracker.Draw(& dc);
	m_Tracker.m_Options ^= option;
	m_Tracker.Draw(& dc);
}

void CQTTDemoView::OnUpdateOptions(CCmdUI *pCmdUI)
{
	UINT nID = pCmdUI->m_nID - ID_OPTIONS_ROTATE;
	UINT option = 0x0001 << nID;
	pCmdUI->SetCheck(m_Tracker.m_Options & option);
}

void CQTTDemoView::OnOptionsColors(UINT nID)
{
	COLORREF * pColors[] =
	{
		& m_Tracker.m_colorMark,
		& m_Tracker.m_colorTrack,
		& m_Tracker.m_colorPath,
		& m_Tracker.m_colorHandles,
		& m_Tracker.m_colorCenter
	};

	nID -= ID_COLORS_MARKRECTANGLE;

	CColorDialog dlg(* pColors[nID], 0, this);
	if (dlg.DoModal() == IDOK)
	{
		CClientDC dc(this);
		OnPrepareDC(& dc);
		m_Tracker.Draw(& dc);
		* pColors[nID] = dlg.GetColor();
		m_Tracker.Draw(& dc);
	}
}

void CQTTDemoView::OnOptionsSizes()
{
	CSizesDlg dlg(this);
	m_Tracker.GetMetrics(dlg.m_HandleSize, dlg.m_InnerMargin, dlg.m_OuterMargin);
	if (dlg.DoModal() == IDOK)
	{
		CClientDC dc(this);
		OnPrepareDC(& dc);
		m_Tracker.SetMetrics(dlg.m_HandleSize, dlg.m_InnerMargin, dlg.m_OuterMargin, & dc);
	}
}

void CQTTDemoView::InitPensAndBrushes(void)
{
	m_pPens[0] = new Pen((ARGB) Color::Gold, 3.0f);
	m_pPens[1] = new Pen((ARGB) Color::DarkGoldenrod, 2.0f);
	m_pPens[2] = new Pen((ARGB) Color::DarkGreen, 1.5f);
	m_pPens[2]->SetDashStyle(DashStyleDot);
	m_pPens[3] = new Pen((ARGB) Color::CadetBlue, 2.5f);
	m_pPens[4] = new Pen((ARGB) Color::DeepSkyBlue, 1.0f);
	m_pPens[5] = new Pen((ARGB) Color::Gainsboro, 1.0f);

	m_pBrushes[0] = new SolidBrush((ARGB) Color::SkyBlue);
	m_pBrushes[1] = new SolidBrush((ARGB) Color::Yellow);
	m_pBrushes[2] = new SolidBrush(Color(180, 0, 190, 120));
	m_pBrushes[3] = new SolidBrush(Color(50, 12, 80, 80));
	m_pBrushes[4] = new SolidBrush(Color(40, 0, 180, 180));
	m_pBrushes[5] = new SolidBrush((ARGB) Color::Gold);
}

// Quick & dirty functions to draw the objects

void CQTTDemoView::InitSillyObjects(void)
{
	CString s(_T("Test"));
	CString sFont(_T("Georgia"));
	FontFamily ff(sFont.AllocSysString());
	StringFormat sf;
	Point p(200, 200);

	MakeStarPath(m_Paths[0], 7, 40, 120);
	Matrix mat;
	mat.Translate(700, 150);
	m_Paths[0].Transform(& mat);

	MakeSmiley(m_Paths[1]);
	mat.Reset();
	mat.Translate(400, 300);
	mat.Scale(0.2f, 0.2f);
	mat.Rotate(- 15.0f);
	m_Paths[1].Transform(& mat);

	Rect rc2(120, 80, 160, 65);
	m_Paths[2].Reset();
	m_Paths[2].AddRectangle(rc2);

	m_Paths[3].Reset();
	m_Paths[3].AddString(s.AllocSysString(), s.GetLength(), &ff, FontStyleBold, 100.0F, p, & sf);

	Rect rc4(420, -240, 120, 265);
	m_Paths[4].Reset();
	m_Paths[4].AddEllipse(rc4);
	mat.Reset();
	mat.Rotate(30.0f);
	m_Paths[4].Transform(& mat);

	MakeStarPath(m_Paths[5], 5, 12, 60);
	mat.Reset();
	mat.Translate(224, 320);
	m_Paths[5].Transform(& mat);
}

void CQTTDemoView::MakeStarPath(GraphicsPath& path, int points, int innerRadius, int outerRadius)
{
	path.Reset();

	REAL phi = 2 * (REAL) atan(1.0f);	// 90 degrees
	REAL dPhi = 2 * phi / points;

	PointF * pnt = new PointF[points * 2];

	for (int i = 0; i < points; i++)
	{
		pnt[2 * i].X = (REAL) (innerRadius * sin(phi - dPhi));
		pnt[2 * i].Y = (REAL) (innerRadius * cos(phi - dPhi));
		pnt[2 * i + 1].X = (REAL) (outerRadius * sin(phi));
		pnt[2 * i + 1].Y = (REAL) (outerRadius * cos(phi));
		phi += 2 * dPhi;
	}
	path.AddLines(pnt, points * 2);
	path.CloseFigure();

	delete[] pnt;
}

void CQTTDemoView::MakeSmiley(GraphicsPath& path)
{
	path.Reset();	
	path.AddEllipse(200, 350, 400, 400);
	Rect rcEye(330, 520, 10, 10);
	path.AddEllipse(rcEye);
	rcEye.Offset(140, 0);
	path.AddEllipse(rcEye);
	Rect rcMouth(370, 590, 60, 60);
	path.AddArc(rcMouth, 0.0f, 180.0f);
	path.CloseFigure();
}
