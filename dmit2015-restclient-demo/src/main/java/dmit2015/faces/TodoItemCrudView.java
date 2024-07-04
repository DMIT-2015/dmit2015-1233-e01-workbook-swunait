package dmit2015.faces;

import dmit2015.restclient.TodoItem;
import dmit2015.restclient.TodoItemMpRestClient;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.omnifaces.util.Messages;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.List;

@Named("currentTodoItemCrudView")
@ViewScoped
public class TodoItemCrudView implements Serializable {

    @Inject
    @RestClient
    private TodoItemMpRestClient _todoItemMpRestClient;

    @Getter
    private List<TodoItem> todoItemList;

    @Getter
    @Setter
    private TodoItem selectedTodoItem;

    @Getter
    @Setter
    private Long editId;

    @PostConstruct  // After @Inject is complete
    public void init() {
        try {
            todoItemList = _todoItemMpRestClient.findAll();
        } catch (Exception ex) {
            Messages.addGlobalError(ex.getMessage());
        }
    }

    public void onOpenNew() {
        selectedTodoItem = new TodoItem();
        editId = null;
    }

    public void onSave() {
        if (editId == null) {
            try {
                Response response = _todoItemMpRestClient.create(selectedTodoItem);
                String location = response.getHeaderString("Location");
                String idValue = location.substring(location.lastIndexOf("/") + 1);
                Messages.addFlashGlobalInfo("Create was successful. {0}", idValue);
                todoItemList = _todoItemMpRestClient.findAll();
            } catch (Exception e) {
                e.printStackTrace();
                Messages.addGlobalError("Create was not successful. {0}", e.getMessage());
            }
        } else {
            try {
                _todoItemMpRestClient.update(editId, selectedTodoItem);
                Messages.addFlashGlobalInfo("Update was successful.");
            } catch (Exception e) {
                e.printStackTrace();
                Messages.addGlobalError("Update was not successful.");
            }
        }

        PrimeFaces.current().executeScript("PF('manageTodoItemDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-TodoItems");
    }

    public void onDelete() {
        try {
            _todoItemMpRestClient.delete(editId);
            editId = null;
            selectedTodoItem = null;
            Messages.addGlobalInfo("Delete was successful.");
            todoItemList = _todoItemMpRestClient.findAll();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-TodoItems");
        } catch (Exception e) {
            e.printStackTrace();
            Messages.addGlobalError("Delete not successful.");
        }
    }

}