#ifndef BBST_H_INCLUDED
#define BBST_H_INCLUDED
#include <iostream>

enum Boolean {FALSE, TRUE};

template <class Type>
class Element
{
public:
    Type key;

};

template <class Type> class BST;//前置声明
template <class Type>
class BstNode//树节点
{
    friend class BST<Type>;
//private:
public:
    Element<Type> data;
    Element<Type> data1;
    BstNode* LeftChild;
    BstNode* RightChild;
    void display(int i );
};
template <class Type>
class BST//
{
public:
    BST(BstNode<Type> *init = 0)
    {
        root = init;
    }

    Boolean Insert(const Element<Type>& x);
    BstNode<Type>* Search(const Element<Type>& x);
    BstNode<Type>* Search(BstNode<Type>*,const Element<Type>& x);
    BstNode<Type>* IterSearch(const Element <Type>&x);
    void display()
    {
        std::cout << std::endl;
        if(root)
            root->display(1);
        else
            std::cout << "is Empty" << "\n";
    }
private:
    BstNode<Type> *root;

};

template<class Type>
void BstNode<Type>::display(int i)
{
    std::cout << "Position:" << i << ", data.key = " << data.key << std::endl;
    if(LeftChild) LeftChild->display(2*i);
    if(RightChild) RightChild->display(2*i+1);
}

template<class Type>

Boolean BST<Type>::Insert(const Element<Type> &x)
{
    BstNode<Type> *p = root;
    BstNode<Type> *q = 0;//q指向p的父结点
    //insert 之前要先查找
    while(p)
    {
        q = p;
        if(x.key == p-> data.key) return FALSE;//发生重复
        if(x.key < p-> data.key) p = p->LeftChild;
        else
        if(x.key > p-> data.key) p = p->RightChild;
    }

    //找到的位置是q
    p = new BstNode<Type>;
    p->LeftChild = p->RightChild = 0;
    p->data = x;

    //p接到q上
    if(!root) root = p;
    else if(x.key < q->data.key) q->LeftChild = p;
    else q->RightChild = p;
    return TRUE;
}


template <class Type>
BstNode<Type>* BST<Type>::Search(const Element<Type> &x)
{
    return Search(root,x);
}

template <class Type>
BstNode<Type>* BST<Type>::Search(BstNode<Type>*b, const Element<Type> &x)
{
    if(!b) return 0;
    if(x.key == b->data.key) return b;
    if(x.key < b->data.key) return Search(b->LeftChild,x);
    return Search(b->RightChild,x);

}

template<class Type>
BstNode<Type>* BST<Type>::IterSearch(const Element<Type> &x)
{
    for(BstNode<Type>* t = root; t;)
    {
        if(x.key == t->data.key) return t;
        if(x.key < t->data.key) t = t->LeftChild;
        else t = t->RightChild;
    }
}


#endif // BBST_H_INCLUDED
