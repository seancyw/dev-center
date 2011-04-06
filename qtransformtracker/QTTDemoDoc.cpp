// QTTDemoDoc.cpp : implementation of the CQTTDemoDoc class
//

#include "stdafx.h"
#include "QTTDemo.h"

#include "QTTDemoDoc.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

// Nothing changed here, fresh from the box.
// CQTTDemoDoc isn't really used.

// CQTTDemoDoc

IMPLEMENT_DYNCREATE(CQTTDemoDoc, CDocument)

BEGIN_MESSAGE_MAP(CQTTDemoDoc, CDocument)
END_MESSAGE_MAP()


// CQTTDemoDoc construction/destruction

CQTTDemoDoc::CQTTDemoDoc()
{
	// TODO: add one-time construction code here

}

CQTTDemoDoc::~CQTTDemoDoc()
{
}

BOOL CQTTDemoDoc::OnNewDocument()
{
	if (!CDocument::OnNewDocument())
		return FALSE;

	// TODO: add reinitialization code here
	// (SDI documents will reuse this document)

	return TRUE;
}




// CQTTDemoDoc serialization

void CQTTDemoDoc::Serialize(CArchive& ar)
{
	if (ar.IsStoring())
	{
		// TODO: add storing code here
	}
	else
	{
		// TODO: add loading code here
	}
}


// CQTTDemoDoc diagnostics

#ifdef _DEBUG
void CQTTDemoDoc::AssertValid() const
{
	CDocument::AssertValid();
}

void CQTTDemoDoc::Dump(CDumpContext& dc) const
{
	CDocument::Dump(dc);
}
#endif //_DEBUG


// CQTTDemoDoc commands
