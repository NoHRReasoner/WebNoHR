package edu.stanford.bmir.protege.web.client.nohrpopupviews;

import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.lang.DisplayNameSettingsManager;
import edu.stanford.bmir.protege.web.client.library.dlg.DialogButton;
import edu.stanford.bmir.protege.web.client.library.modal.ModalManager;
import edu.stanford.bmir.protege.web.client.library.modal.ModalPresenter;
import edu.stanford.bmir.protege.web.client.project.ActiveProjectManager;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.CreateRulesAction;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrrules.NohrRule;
import edu.stanford.bmir.protege.web.shared.nohrrules.RuleAlreadyExistException;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class CreateRulePresenter {

    @Nonnull
    private final DispatchServiceManager dispatchServiceManager;

    @Nonnull
    private final ProjectId projectId;

    @Nonnull
    private final CreateRuleDialogView view;

    @Nonnull
    private final ModalManager modalManager;

    @Nonnull
    private final ActiveProjectManager activeProjectManager;

    @Nonnull
    private final DisplayNameSettingsManager displayNameSettingsManager;

    @Nonnull
    private final Messages messages;

    private Optional<String> currentLangTag = Optional.empty();

    @Inject
    public CreateRulePresenter(@Nonnull DispatchServiceManager dispatchServiceManager,
                               @Nonnull ProjectId projectId,
                               @Nonnull CreateRuleDialogView view,
                               @Nonnull ModalManager modalManager,
                               @Nonnull ActiveProjectManager activeProjectManager,
                               @Nonnull DisplayNameSettingsManager displayNameSettingsManager,
                               @Nonnull Messages messages) {
        this.dispatchServiceManager = checkNotNull(dispatchServiceManager);
        this.projectId = checkNotNull(projectId);
        this.view = view;
        this.modalManager = modalManager;
        this.activeProjectManager = checkNotNull(activeProjectManager);
        this.displayNameSettingsManager = checkNotNull(displayNameSettingsManager);
        this.messages = checkNotNull(messages);

    }

    public void createRules(@Nonnull RulesCreatedHandler rulesCreatedHandler,
                            @Nonnull ActionFactory actionFactory) {
        view.clear();
        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalPresenter.setTitle(messages.create() + " ");
        modalPresenter.setView(view);
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        modalPresenter.setPrimaryButton(DialogButton.CREATE);
        modalPresenter.setButtonHandler(DialogButton.CREATE, closer -> {
            handleCreateRules(view.getText(), actionFactory, rulesCreatedHandler);
            closer.closeModal();
        });
        modalManager.showModal(modalPresenter);

    }


    private void handleCreateRules(@Nonnull String enteredText,
                                   @Nonnull ActionFactory actionFactory,
                                   @Nonnull RulesCreatedHandler rulesCreatedHandler) throws RuleAlreadyExistException {

        //throwRuleAlreadyExistIfNecessary(rules, enteredText);

        //Usar o closerModal neste método de forma a controlar quando a janela é fechada
        //Assim é possível fechar um popup e abrir outro e fazer as verificações da regra duplicada neste método

        CreateRulesAction action = actionFactory.createAction(projectId,
                enteredText);
        dispatchServiceManager.execute(action,
                result -> rulesCreatedHandler.handleRulesCreated(result.getRule(), result.getCode()));
    }

    public interface ActionFactory {


        CreateRulesAction createAction(
                @Nonnull ProjectId projectId,
                @Nonnull String createFromText);
    }


    public interface RulesCreatedHandler {
        void handleRulesCreated(NohrRule rule, NohrResponseCodes code);
    }
}
