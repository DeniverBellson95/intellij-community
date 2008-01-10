package com.intellij.psi.xml;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.jsp.JspTokenType;
import com.intellij.psi.tree.DefaultRoleFinder;
import com.intellij.psi.tree.RoleFinder;
import com.intellij.xml.util.XmlTagUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface XmlChildRole {
  RoleFinder START_TAG_NAME_FINDER = new RoleFinder() {
    public ASTNode findChild(@NotNull ASTNode parent) {
      final PsiElement element = XmlTagUtil.getStartTagNameElement((XmlTag)parent.getPsi());
      return element == null ? null : element.getNode();
    }
  };

  RoleFinder CLOSING_TAG_NAME_FINDER = new RoleFinder() {
    @Nullable
    public ASTNode findChild(@NotNull ASTNode parent) {
      final PsiElement element = XmlTagUtil.getEndTagNameElement((XmlTag)parent.getPsi());
      return element == null ? null : element.getNode();
    }
  };

  RoleFinder DOCUMENT_FINDER = new RoleFinder() {
    public ASTNode findChild(@NotNull ASTNode parent) {
      ASTNode oldDocument = TreeUtil.findChild(parent, XmlElementType.XML_DOCUMENT);
      if(oldDocument == null) oldDocument = TreeUtil.findChild(parent, XmlElementType.HTML_DOCUMENT);
      return oldDocument;
    }
  };

  RoleFinder ATTRIBUTE_VALUE_FINDER = new DefaultRoleFinder(XmlElementType.XML_ATTRIBUTE_VALUE);
  RoleFinder CLOSING_TAG_START_FINDER = new DefaultRoleFinder(XmlTokenType.XML_END_TAG_START);
  RoleFinder EMPTY_TAG_END_FINDER = new DefaultRoleFinder(XmlTokenType.XML_EMPTY_ELEMENT_END);
  RoleFinder ATTRIBUTE_NAME_FINDER = new DefaultRoleFinder(XmlTokenType.XML_NAME);
  RoleFinder ATTRIBUTE_VALUE_VALUE_FINDER = new DefaultRoleFinder(XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN);
  RoleFinder START_TAG_END_FINDER = new DefaultRoleFinder(XmlTokenType.XML_TAG_END, JspTokenType.JSP_SCRIPTLET_START,
                                                                              JspTokenType.JSP_EXPRESSION_START, JspTokenType.JSP_DECLARATION_START);
  RoleFinder START_TAG_START_FINDER = new DefaultRoleFinder(XmlTokenType.XML_START_TAG_START);
  RoleFinder PROLOG_FINDER = new DefaultRoleFinder(XmlElementType.XML_PROLOG);


  int XML_DOCUMENT = 223;
  int XML_TAG_NAME = 224;
  int XML_PROLOG = 225;
  int XML_DOCTYPE = 226;
  int XML_DOCTYPE_PUBLIC = 227;
  int XML_DOCTYPE_SYSTEM = 228;
  int XML_NAME = 229;
  int XML_ELEMENT_CONTENT_SPEC = 230;
  int XML_CONTENT_ANY = 231;
  int XML_CONTENT_EMPTY = 232;
  int XML_PCDATA = 233;
  int XML_ATT_REQUIRED = 234;
  int XML_ATT_IMPLIED = 235;
  int XML_ATT_FIXED = 236;
  int XML_DEFAULT_VALUE = 237;
  int XML_ENUMERATED_TYPE = 238;
  int XML_ATTRIBUTE = 240;
  int XML_TAG = 241;
  int XML_ATTRIBUTE_VALUE = 243;
  int HTML_DOCUMENT = 252;
}
