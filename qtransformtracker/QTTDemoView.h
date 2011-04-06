// QTTDemoView.h
//

#pragma once
#include "QTransformTracker.h"

class CQTTDemoView : public CScrollView
{
protected:
	CQTTDemoView();
	virtual ~CQTTDemoView();

	virtual void OnDraw(CDC* pDC);
	virtual void OnInitialUpdate();
	virtual void OnPrepareDC(CDC* pDC, CPrintInfo* pInfo = NULL);

	afx_msg void OnLButtonDown(UINT nFlags, CPoint point);
	afx_msg BOOL OnSetCursor(CWnd* pWnd, UINT nHitTest, UINT message);
	afx_msg void OnKeyDown(UINT nChar, UINT nRepCnt, UINT nFlags);
	afx_msg void OnKeyUp(UINT nChar, UINT nRepCnt, UINT nFlags);
	afx_msg void OnUpdateIndicatorTracker(CCmdUI *pCmdUI);
	afx_msg void OnOptions(UINT nID);
	afx_msg void OnUpdateOptions(CCmdUI *pCmdUI);
	afx_msg void OnOptionsColors(UINT nID);
	afx_msg void OnOptionsSizes();

	int HitTest(CPoint point);
	void InitPensAndBrushes(void);
	void InitSillyObjects(void);
	void MakeStarPath(GraphicsPath& path, int points, int innerRadius, int outerRadius);
	void MakeSmiley(GraphicsPath& path);

	// Our very versatile transformation tracker class:
	QTransformTracker m_Tracker;

	int m_iPath;
	GraphicsPath m_Paths[6];
	Brush * m_pBrushes[6];
	Pen * m_pPens[6];

	DECLARE_MESSAGE_MAP()
	DECLARE_DYNCREATE(CQTTDemoView)
};
