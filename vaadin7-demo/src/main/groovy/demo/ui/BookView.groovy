package demo.ui

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.ui.Button
import com.vaadin.ui.CustomComponent
import com.vaadin.ui.FormLayout
import com.vaadin.ui.Window
import com.vaadin.v7.ui.Grid
import com.vaadin.v7.ui.HorizontalLayout
import com.vaadin.v7.ui.Label
import com.vaadin.v7.ui.VerticalLayout
import demo.Book
import org.vaadin.grails.data.fieldgroup.DomainFieldGroup
import org.vaadin.grails.data.util.DomainItem
import org.vaadin.grails.data.util.DomainItemContainer
import org.vaadin.openesignforms.ckeditor.CKEditorConfig
import org.vaadin.openesignforms.ckeditor.CKEditorTextField

class BookView extends CustomComponent implements View {

    class BookEditor extends Window {

        DomainFieldGroup<Book> fieldGroup

        BookEditor() {
            fieldGroup = new DomainFieldGroup(Book)
            def form = new FormLayout()
            form.setSizeUndefined()
            form.setMargin(true)

            form.addComponent(fieldGroup.buildAndBind('Title', 'title'))
            form.addComponent(fieldGroup.buildAndBind('ISBN', 'isbn'))
            form.addComponent(fieldGroup.buildAndBind('Author', 'author'))
            def ckcfg = new CKEditorConfig()
            ckcfg.disableResizeEditor()
            ckcfg.disableElementsPath()
            ckcfg.disableSpellChecker()
            ckcfg.toolbarCanCollapse = true
            ckcfg.toolbarStartupExpanded = false
            def ck = new CKEditorTextField(ckcfg)
            ck.setSizeFull()
            fieldGroup.bind(ck, 'description')
            form.addComponent(ck)

            def saveButton = new Button("Save", new Button.ClickListener() {
                @Override
                void buttonClick(Button.ClickEvent event) {
                    if (fieldGroup.commit(true)) {
                        def item = fieldGroup.itemDataSource
                        item.save(true)
                        reloadBooks()
                        close()
                    }

                }
            })
            def closeButton = new Button("Close", new Button.ClickListener() {
                @Override
                void buttonClick(Button.ClickEvent event) {
                    fieldGroup.discard()
                    close()
                }
            })
            def buttonBar = new HorizontalLayout()
            buttonBar.setSpacing(true)
            saveButton.styleName = "primary"
            buttonBar.addComponent(saveButton)
            closeButton.styleName = "quiet"
            buttonBar.addComponent(closeButton)
            form.addComponent(buttonBar)
            content = form
            caption = "Book"
        }

        void open(DomainItem<Book> itemDataSource) {
            fieldGroup.itemDataSource = itemDataSource

            def ui = com.vaadin.v7.ui.UI.current
            if (!ui.windows.contains(this)) {
                ui.addWindow(this)

            }
            center()
        }

        void open() {
            open(new DomainItem(Book))
        }
    }

    DomainItemContainer<Book> bookContainer
    Grid bookGrid

    BookView() {
        def editor = new BookEditor()

        def root = new VerticalLayout()
        root.setMargin(true)
        root.setSpacing(true)

        def title = new Label("Books")
        title.styleName = "h2 colored"
        root.addComponent(title)

        bookGrid = new Grid()
        bookGrid.setSelectionMode(Grid.SelectionMode.MULTI)
        bookContainer = new DomainItemContainer(Book)
        root.addComponent(bookGrid)

        def createButton = new Button("New Book", new Button.ClickListener() {
            @Override
            void buttonClick(Button.ClickEvent event) {
                editor.open()
            }
        })

        def deleteButton = new Button("Delete Book(s)", new Button.ClickListener() {
            @Override
            void buttonClick(Button.ClickEvent event) {
                bookGrid.selectedRows.each { itemId ->
                    def item = bookContainer.getItem(itemId)
                    item.delete(true)
                }
                bookGrid.selectionModel.reset()
                reloadBooks()
            }
        })

        def buttonBar = new HorizontalLayout()
        buttonBar.setSpacing(true)
        createButton.styleName = "primary"
        buttonBar.addComponent(createButton)
        deleteButton.styleName = "danger"
        buttonBar.addComponent(deleteButton)

        root.addComponent(buttonBar)
        compositionRoot = root
    }

    void reloadBooks() {
        bookContainer.removeAllItems()
        Book.list().each { book ->
            bookContainer.addItem(book)
        }
        bookGrid.containerDataSource = bookContainer
    }

    @Override
    void enter(ViewChangeListener.ViewChangeEvent event) {
        reloadBooks()
    }
}
