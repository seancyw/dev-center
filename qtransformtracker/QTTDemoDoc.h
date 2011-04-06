// QTTDemoDoc.h : interface of the CQTTDemoDoc class
//

// Nothing changed here, fresh from the box.
// CQTTDemoDoc isn't really used.

#pragma once

class CQTTDemoDoc : public CDocument
{
protected: // create from serialization only
	CQTTDemoDoc();
	DECLARE_DYNCREATE(CQTTDemoDoc)

// Attributes
public:

// Operations
public:

// Overrides
	public:
	virtual BOOL OnNewDocument();
	virtual void Serialize(CArchive& ar);

// Implementation
public:
	virtual ~CQTTDemoDoc();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

protected:

// Generated message map functions
protected:
	DECLARE_MESSAGE_MAP()
};


