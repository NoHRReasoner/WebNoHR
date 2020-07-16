package edu.stanford.bmir.protege.web.client.nohrpopupviews;

import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.library.dlg.DialogButton;
import edu.stanford.bmir.protege.web.client.library.modal.ModalManager;
import edu.stanford.bmir.protege.web.client.library.modal.ModalPresenter;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.UpdateRulesAction;
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
public class UpdateRulePresenter {

    @Nonnull
    private final DispatchServiceManager dispatchServiceManager;

    @Nonnull
    private final ProjectId projectId;

    @Nonnull
    private final UpdateRuleDialogView view;

    @Nonnull
    private final ModalManager modalManager;

    @Nonnull
    private final Messages messages;

    private Optional<String> currentLangTag = Optional.empty();

    @Inject
    public UpdateRulePresenter(@Nonnull DispatchServiceManager dispatchServiceManager,
                               @Nonnull ProjectId projectId,
                               @Nonnull UpdateRuleDialogView view,
                               @Nonnull ModalManager modalManager,
                               @Nonnull Messages messages) {
        this.dispatchServiceManager = checkNotNull(dispatchServiceManager);
        this.projectId = checkNotNull(projectId);
        this.view = view;
        this.modalManager = modalManager;
        this.messages = checkNotNull(messages);
    }

    public void updateRules(@Nonnull RulesUpdateHandler rulesUpdateHandler,
                            @Nonnull String oldRule,
                            @Nonnull ActionFactory actionFactory) {
        view.clear();
        view.setText(oldRule);
        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalPresenter.setTitle("Update");
        modalPresenter.setView(view);
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        modalPresenter.setPrimaryButton(DialogButton.CREATE);
        modalPresenter.setButtonHandler(DialogButton.CREATE, closer -> {
            handleUpdateRules(view.getText(), oldRule, actionFactory, rulesUpdateHandler);
            closer.closeModal();
        });
        modalManager.showModal(modalPresenter);

    }


    private void handleUpdateRules(@Nonnull String newRule,
                                   @Nonnull String oldRule,
                                   @Nonnull ActionFactory actionFactory,
                                   @Nonnull RulesUpdateHandler rulesUpdateHandler) throws RuleAlreadyExistException {

        //throwRuleAlreadyExistIfNecessary(rules, enteredText);

        //Usar o closerModal neste método de forma a controlar quando a janela é fechada
        //Assim é possível fechar um popup e abrir outro e fazer as verificações da regra duplicada neste método

        UpdateRulesAction action = actionFactory.createAction(projectId,
                newRule, oldRule);
        dispatchServiceManager.execute(action,
                result -> rulesUpdateHandler.handleRulesUpdated(result.getNewRule(), result.getOldRule(), result.getCode()));
    }

    public interface ActionFactory {


        UpdateRulesAction createAction(
                @Nonnull ProjectId projectId,
                @Nonnull String newRule,
                @Nonnull String oldRule);
    }


    public interface RulesUpdateHandler {
        void handleRulesUpdated(NohrRule newRule, NohrRule oldRule, NohrResponseCodes code);
    }
}
