package controllers;

import exceptions.DatabaseException;
import exceptions.DuplicateEmailException;
import models.Member;
import services.MemberService;
import utils.InputValidator;
import utils.MessageHelper;
import utils.TableFormatter;

import java.util.List;
import java.util.Optional;

/**
 * Controller for member operations
 */
public class MemberController {

    private final MemberService memberService;

    public MemberController() {
        this.memberService = new MemberService();
    }

    /**
     * Create a new member
     */
    public boolean createMember(String name, String email, String phone, String address) {
        try {
            // Validate input
            if (InputValidator.isNullOrEmpty(name)) {
                MessageHelper.showError("El nombre es requerido");
                return false;
            }

            if (InputValidator.isNullOrEmpty(email)) {
                MessageHelper.showError("El email es requerido");
                return false;
            }

            if (!InputValidator.isValidEmail(email)) {
                MessageHelper.showError("Formato de email inválido");
                return false;
            }

            if (!InputValidator.isNullOrEmpty(phone) && !InputValidator.isValidPhone(phone)) {
                MessageHelper.showError("Formato de teléfono inválido");
                return false;
            }

            // Create member
            Member member = memberService.createMember(name, email, phone, address);

            MessageHelper.showSuccess("Socio creado exitosamente:\n" +
                    "ID: " + member.getIdMember() + "\n" +
                    "Nombre: " + member.getName());
            return true;

        } catch (DuplicateEmailException e) {
            MessageHelper.showError("El email ya está registrado: " + email);
            return false;
        } catch (DatabaseException e) {
            MessageHelper.showError("Error al crear el socio: " + e.getMessage());
            return false;
        } catch (Exception e) {
            MessageHelper.showError("Error inesperado: " + e.getMessage());
            return false;
        }
    }

    /**
     * View all members
     */
    public void viewAllMembers() {
        try {
            List<Member> members = memberService.getAllMembers();

            if (members.isEmpty()) {
                MessageHelper.showInfo("No hay socios registrados en el sistema");
                return;
            }

            String table = TableFormatter.formatMembersTable(members);
            MessageHelper.showScrollableData("Lista de Socios", table);

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al cargar los socios: " + e.getMessage());
        }
    }

    /**
     * View active members only
     */
    public void viewActiveMembers() {
        try {
            List<Member> members = memberService.getActiveMembers();

            if (members.isEmpty()) {
                MessageHelper.showInfo("No hay socios activos en el sistema");
                return;
            }

            String table = TableFormatter.formatMembersTable(members);
            MessageHelper.showScrollableData("Socios Activos", table);

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al cargar los socios: " + e.getMessage());
        }
    }

    /**
     * View member details
     */
    public void viewMemberDetails(String memberIdStr) {
        try {
            if (!InputValidator.isPositiveInteger(memberIdStr)) {
                MessageHelper.showError("ID de socio inválido");
                return;
            }

            Integer memberId = InputValidator.parseIntSafely(memberIdStr);
            Optional<Member> memberOpt = memberService.findMemberById(memberId);

            if (memberOpt.isEmpty()) {
                MessageHelper.showError("Socio no encontrado con ID: " + memberId);
                return;
            }

            String details = TableFormatter.formatMemberDetails(memberOpt.get());
            MessageHelper.showScrollableData("Detalles del Socio", details);

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al buscar el socio: " + e.getMessage());
        }
    }

    /**
     * Update member
     */
    public boolean updateMember(String memberIdStr, String name, String email, String phone, String address) {
        try {
            if (!InputValidator.isPositiveInteger(memberIdStr)) {
                MessageHelper.showError("ID de socio inválido");
                return false;
            }

            if (!InputValidator.isValidEmail(email)) {
                MessageHelper.showError("Formato de email inválido");
                return false;
            }

            Integer memberId = InputValidator.parseIntSafely(memberIdStr);

            // Get existing member
            Optional<Member> memberOpt = memberService.findMemberById(memberId);
            if (memberOpt.isEmpty()) {
                MessageHelper.showError("Socio no encontrado");
                return false;
            }

            Member member = memberOpt.get();
            member.setName(name);
            member.setEmail(email);
            member.setPhone(phone);
            member.setAddress(address);

            boolean updated = memberService.updateMember(member);

            if (updated) {
                MessageHelper.showSuccess("Socio actualizado exitosamente");
            } else {
                MessageHelper.showError("No se pudo actualizar el socio");
            }

            return updated;

        } catch (DuplicateEmailException e) {
            MessageHelper.showError("El email ya está registrado por otro socio");
            return false;
        } catch (DatabaseException e) {
            MessageHelper.showError("Error al actualizar el socio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Activate member
     */
    public boolean activateMember(String memberIdStr) {
        try {
            if (!InputValidator.isPositiveInteger(memberIdStr)) {
                MessageHelper.showError("ID de socio inválido");
                return false;
            }

            Integer memberId = InputValidator.parseIntSafely(memberIdStr);
            boolean activated = memberService.activateMember(memberId);

            if (activated) {
                MessageHelper.showSuccess("Socio activado exitosamente");
            } else {
                MessageHelper.showError("No se pudo activar el socio");
            }

            return activated;

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al activar el socio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deactivate member
     */
    public boolean deactivateMember(String memberIdStr) {
        try {
            if (!InputValidator.isPositiveInteger(memberIdStr)) {
                MessageHelper.showError("ID de socio inválido");
                return false;
            }

            Integer memberId = InputValidator.parseIntSafely(memberIdStr);
            boolean deactivated = memberService.deactivateMember(memberId);

            if (deactivated) {
                MessageHelper.showSuccess("Socio desactivado exitosamente");
            } else {
                MessageHelper.showError("No se pudo desactivar el socio");
            }

            return deactivated;

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al desactivar el socio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete member
     */
    public boolean deleteMember(String memberIdStr) {
        try {
            if (!InputValidator.isPositiveInteger(memberIdStr)) {
                MessageHelper.showError("ID de socio inválido");
                return false;
            }

            Integer memberId = InputValidator.parseIntSafely(memberIdStr);

            if (!MessageHelper.showConfirmation("¿Está seguro de eliminar este socio?\nID: " + memberId)) {
                return false;
            }

            boolean deleted = memberService.deleteMember(memberId);

            if (deleted) {
                MessageHelper.showSuccess("Socio eliminado exitosamente");
            } else {
                MessageHelper.showError("No se pudo eliminar el socio");
            }

            return deleted;

        } catch (DatabaseException e) {
            MessageHelper.showError("Error al eliminar el socio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all members (for exports or other operations)
     */
    public List<Member> getAllMembers() throws DatabaseException {
        return memberService.getAllMembers();
    }

    /**
     * Get active members list
     */
    public List<Member> getActiveMembers() throws DatabaseException {
        return memberService.getActiveMembers();
    }
}