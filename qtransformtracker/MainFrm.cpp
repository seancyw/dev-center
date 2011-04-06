// MainFrm.cpp
//

#include "stdafx.h"
#include "Resource.h"

#include "MainFrm.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

const int TimerID = 3;
const int TimerPeriod = 4000;
const int FirstMessage = 1000;

IMPLEMENT_DYNCREATE(CMainFrame, CFrameWnd)

BEGIN_MESSAGE_MAP(CMainFrame, CFrameWnd)
	ON_WM_CREATE()
	ON_WM_DESTROY()
	ON_WM_TIMER()
END_MESSAGE_MAP()

static UINT indicators[] =
{
	ID_SEPARATOR,
	ID_INDICATOR_TRACKER	// indicator for QTransformTracker in CQTTDemoView
};

CMainFrame::CMainFrame()
: m_iMessage(FirstMessage)
{
}

CMainFrame::~CMainFrame()
{
}

int CMainFrame::OnCreate(LPCREATESTRUCT lpCreateStruct)
{
	if (CFrameWnd::OnCreate(lpCreateStruct) == -1)
		return -1;
	if (!m_wndStatusBar.Create(this) ||
		!m_wndStatusBar.SetIndicators(indicators,
		  sizeof(indicators)/sizeof(UINT)))
		return -1;

	SetTimer(TimerID, TimerPeriod, NULL);
	return 0;
}

void CMainFrame::OnDestroy()
{
	CFrameWnd::OnDestroy();
	KillTimer(TimerPeriod);
}

void CMainFrame::OnTimer(UINT nIDEvent)
{
	if (nIDEvent == TimerID)
	{
		CString s;
		if (! s.LoadString(m_iMessage++))
		{
			m_iMessage = FirstMessage;
			s.LoadString(m_iMessage++);
		}
		SetMessageText(s);
	}
	CFrameWnd::OnTimer(nIDEvent);
}
