package views;

import controllers.MemberController;

import javax.swing.*;

/**
 * Vista para la gestión de socios (usa JOptionPane)
 */
public class MemberView {

    private final MemberController memberController;

    public MemberView(MemberController memberController) {
        this.memberController = memberController;
    }

    public void showMenu() {
        String option;
        do {
            option = JOptionPane.showInputDialog(null,
                    """
                    👥 GESTIÓN DE SOCIOS
                    
                    1. Registrar socio
                    2. Ver todos los socios
                    3. Ver socios activos
                    4. Ver detalles de un socio
                    5. Actualizar socio
                    6. Activar socio
                    7. Desactivar socio
                    8. Eliminar socio
                    0. Volver al menú principal
                    
                    Seleccione una opción:
                    """,
                    "Menú de Socios", JOptionPane.PLAIN_MESSAGE);

            if (option == null) break; // cancelar

            switch (option) {
                case "1" -> createMember();
                case "2" -> memberController.viewAllMembers();
                case "3" -> memberController.viewActiveMembers();
                case "4" -> viewMemberDetails();
                case "5" -> updateMember();
                case "6" -> activateMember();
                case "7" -> deactivateMember();
                case "8" -> deleteMember();
                case "0" -> JOptionPane.showMessageDialog(null, "Volviendo al menú principal...");
                default -> JOptionPane.showMessageDialog(null, "Opción inválida");
            }
        } while (!"0".equals(option));
    }

    private void createMember() {
        String name = JOptionPane.showInputDialog("Ingrese el nombre del socio:");
        String email = JOptionPane.showInputDialog("Ingrese el email del socio:");
        String phone = JOptionPane.showInputDialog("Ingrese el teléfono (opcional):");
        String address = JOptionPane.showInputDialog("Ingrese la dirección (opcional):");

        if (name == null || email == null) return;

        memberController.createMember(name, email, phone, address);
    }

    private void viewMemberDetails() {
        String memberId = JOptionPane.showInputDialog("Ingrese el ID del socio:");
        if (memberId != null) memberController.viewMemberDetails(memberId);
    }

    private void updateMember() {
        String memberId = JOptionPane.showInputDialog("Ingrese el ID del socio a actualizar:");
        if (memberId == null) return;

        String name = JOptionPane.showInputDialog("Nuevo nombre:");
        String email = JOptionPane.showInputDialog("Nuevo email:");
        String phone = JOptionPane.showInputDialog("Nuevo teléfono:");
        String address = JOptionPane.showInputDialog("Nueva dirección:");

        if (name == null || email == null) return;

        memberController.updateMember(memberId, name, email, phone, address);
    }

    private void activateMember() {
        String memberId = JOptionPane.showInputDialog("Ingrese el ID del socio a activar:");
        if (memberId != null) memberController.activateMember(memberId);
    }

    private void deactivateMember() {
        String memberId = JOptionPane.showInputDialog("Ingrese el ID del socio a desactivar:");
        if (memberId != null) memberController.deactivateMember(memberId);
    }

    private void deleteMember() {
        String memberId = JOptionPane.showInputDialog("Ingrese el ID del socio a eliminar:");
        if (memberId != null) memberController.deleteMember(memberId);
    }
}
