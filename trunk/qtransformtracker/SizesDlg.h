#pragma once

// CSizesDlg dialog

class CSizesDlg : public CDialog
{
public:
	CSizesDlg(CWnd* pParent = NULL);
	virtual ~CSizesDlg();

	UINT m_HandleSize;
	UINT m_InnerMargin;
	UINT m_OuterMargin;

protected:
	enum { IDD = IDD_SIZES };

	virtual void DoDataExchange(CDataExchange* pDX);
	virtual BOOL OnInitDialog();

	CSpinButtonCtrl c_HandleSize;
	CSpinButtonCtrl c_InnerMargin;
	CSpinButtonCtrl c_OuterMargin;

	DECLARE_MESSAGE_MAP()
	DECLARE_DYNAMIC(CSizesDlg)
};
