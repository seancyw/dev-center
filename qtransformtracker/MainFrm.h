// MainFrm.h
//

#pragma once
class CMainFrame : public CFrameWnd
{
protected:
	CMainFrame();
	virtual ~CMainFrame();

	afx_msg int OnCreate(LPCREATESTRUCT lpCreateStruct);
	afx_msg void OnDestroy();
	afx_msg void OnTimer(UINT nIDEvent);

	CStatusBar  m_wndStatusBar;
	int m_iMessage;

	DECLARE_MESSAGE_MAP()
	DECLARE_DYNCREATE(CMainFrame)
};


