// QTTDemo.h : main header file for the QTTDemo application
//
#pragma once

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "resource.h"       // main symbols


// CQTTDemoApp:
// See QTTDemo.cpp for the implementation of this class
//

class CQTTDemoApp : public CWinApp
{
public:
	CQTTDemoApp();


// Overrides
public:
	virtual BOOL InitInstance();

// Implementation
	afx_msg void OnAppAbout();
	DECLARE_MESSAGE_MAP()

private:
	QGdiPlus m_GdiPlus;
};

extern CQTTDemoApp theApp;