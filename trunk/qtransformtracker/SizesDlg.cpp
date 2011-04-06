// SizesDlg.cpp

#include "stdafx.h"
#include "Resource.h"
#include "SizesDlg.h"

// CSizesDlg dialog

IMPLEMENT_DYNAMIC(CSizesDlg, CDialog)
CSizesDlg::CSizesDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CSizesDlg::IDD, pParent)
	, m_HandleSize(0)
	, m_InnerMargin(0)
	, m_OuterMargin(0)
{
}

CSizesDlg::~CSizesDlg()
{
}

void CSizesDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDCS_HANDLESIZE, c_HandleSize);
	DDX_Control(pDX, IDCS_INNERMARGIN, c_InnerMargin);
	DDX_Control(pDX, IDCS_OUTERMARGIN, c_OuterMargin);
	DDX_Text(pDX, IDC_HANDLESIZE, m_HandleSize);
	DDV_MinMaxUInt(pDX, m_HandleSize, 0, 40);
	DDX_Text(pDX, IDC_INNERMARGIN, m_InnerMargin);
	DDV_MinMaxUInt(pDX, m_InnerMargin, 0, 40);
	DDX_Text(pDX, IDC_OUTERMARGIN, m_OuterMargin);
	DDV_MinMaxUInt(pDX, m_OuterMargin, 0, 40);
}

BEGIN_MESSAGE_MAP(CSizesDlg, CDialog)
END_MESSAGE_MAP()

BOOL CSizesDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	c_HandleSize.SetRange(0, 40);
	c_InnerMargin.SetRange(0, 40);
	c_OuterMargin.SetRange(0, 40);

	return TRUE;
}
