// QTTDemoView.h
//

#pragma once
#include "QTransformTracker.h"

class CQTTDemoView : public CScrollView
{
public:
	static const int PATH_MULTI_STAR	= 0;//0
	static const int PATH_SMILE			= PATH_MULTI_STAR + 1;//1
	static const int PATH_RECTANGLE		= PATH_SMILE + 1;//2
	static const int PATH_TEXT			= PATH_RECTANGLE + 1;//3
	static const int PATH_ELLIPSE		= PATH_TEXT + 1;//4
	static const int PATH_STAR			= PATH_ELLIPSE + 1;//5
	static const int PATH_IMAGE			= PATH_STAR + 1;//6
	static const int PATH_COUNT			= 7;//7

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
	//Add vao mot cai image path, co the cach suy nghi nay khong dung
	//Cai nay co the lay ra tu cai module.
	void MakeImagePath(GraphicsPath& path);

	// Our very versatile transformation tracker class:
	QTransformTracker m_Tracker;

	int m_iPath;
	GraphicsPath m_Paths[PATH_COUNT];
	Brush * m_pBrushes[PATH_COUNT];
	Pen * m_pPens[PATH_COUNT];

	DECLARE_MESSAGE_MAP()
	DECLARE_DYNCREATE(CQTTDemoView)
};
