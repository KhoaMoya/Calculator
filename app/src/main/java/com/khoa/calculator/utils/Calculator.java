package com.khoa.calculator.utils;

import com.khoa.calculator.viewmodel.CalculatorViewModel;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author khoar
 */
public class Calculator {

    String[] operaArray = {".", "+", "-", "*", "/", "%", "(", ")", "^", "sin", "cos", "tan", "sqrt"};
    String[] numberArray = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    String[] keyArray = {"A", "B", "C", "ANS"};
    String AC = "AC";
    String SET = "SET";
    String RESULT = "=";
    String DEL = "Del";
    String ANS = "0";
    String A = "0";
    String B = "0";
    String C = "0";
    boolean SET_FLAG = false;

    ArrayList<String> operaList = new ArrayList<>(Arrays.asList(operaArray));
    ArrayList<String> numberList = new ArrayList<>(Arrays.asList(numberArray));
    ArrayList<String> keyList = new ArrayList<>(Arrays.asList(keyArray));

    public ArrayList<String> nodeList;
    CalculatorViewModel viewModel;

    public Calculator(CalculatorViewModel viewModel) {
        nodeList = new ArrayList<>();
        this.viewModel = viewModel;
    }

//    public  void input() {
//        System.out.println("Nhập phép tính:");
//        Scanner scanner = new Scanner(System.in);
//        String input = "";
//        while (!input.equals("=")) {
//            input = scanner.next();
//            addToNodeList(input);
//            showCalculation();
//        }
//        System.out.println(Arrays.asList(nodeList));
//        String result = calcutor(nodeList);
//        System.out.println(result);
//    }

    public String calcutor(ArrayList<String> list) {
        String result = "";

        ArrayList<String> nodeList = (ArrayList) list.clone();
        int open = 0;
        int close = 0;
        for (int i = 0; i < nodeList.size(); i++) {
            if (nodeList.get(i).equals("(")) {
                open++;
            } else if (nodeList.get(i).equals(")")) {
                close++;
            }
        }

        if (open != close) {
            viewModel.showNotification("Biểu thức lỗi");
        } else {
            // tinh gia tri trong ngoac
            // (64-52)*sin(43/3^2)-sqrt(125/(5*6))+tan(1+2)
            int soNgoac = open;
            for (int u = 0; u < soNgoac; u++) {
                for (int i = 0; i < nodeList.size(); i++) {
                    if (nodeList.get(i).equals("(")) {
                        open = i;
                    } else if (nodeList.get(i).equals(")")) {
                        close = i;
                        if (close - open == 1) {
                            nodeList.remove(open);
                            nodeList.remove(open);
                        } else if (close - open == 2) {
                            if (open == 0) {
                                nodeList.remove(open);
                                nodeList.remove(open + 1);
                            } else {
                                String preNode = nodeList.get(open - 1);
                                if (preNode.equals("sin") || preNode.equals("cos") || preNode.equals("tan") || preNode.equals("sqrt")) {
                                    String rs = calcutorSimple(nodeList.get(open + 1), "", preNode);
                                    nodeList.remove(open - 1);
                                    nodeList.remove(open - 1);
                                    nodeList.remove(open - 1);
                                    nodeList.remove(open - 1);
                                    nodeList.add(open - 1, rs);
                                } else {
                                    nodeList.remove(open);
                                    nodeList.remove(open);
                                }
                            }
                        } else if (close - open >= 3) {
                            ArrayList<String> tempList = new ArrayList();
                            for (int j = open + 1; j < close; j++) {
                                tempList.add(nodeList.get(j));
                            }
                            String rs = calcutorList(tempList);
                            if (open == 0) {
                                for (int j = open; j <= close; j++) {
                                    nodeList.remove(open);
                                }
                                nodeList.add(open, rs);
                            } else {
                                String preNode = nodeList.get(open - 1);
                                if (preNode.equals("sin") || preNode.equals("cos") || preNode.equals("tan") || preNode.equals("sqrt")) {
                                    String r = calcutorSimple(rs, "", preNode);
                                    for (int j = open - 1; j <= close; j++) {
                                        nodeList.remove(open - 1);
                                    }
                                    nodeList.add(open - 1, r);
                                } else {
                                    for (int j = open; j <= close; j++) {
                                        nodeList.remove(open);
                                    }
                                    nodeList.add(open, rs);
                                }
                            }
                        } else {
                            viewModel.showNotification("Biểu thức lỗi");
                        }

                        break;
                    }
                }
            }
            result = calcutorList(nodeList);
        }

        if (result.endsWith(".0")) result = result.substring(0, result.length() - 2);
        if (result.equals("-0")) result = "0";
        return result;
    }

    public String calcutorList(ArrayList<String> list) {
        try {
            // 1+2/5*6^3-8
            // thực hiện các phép tính ưu tiên trước
            int i = 0;
            while (i < list.size()) {
                String node = list.get(i);
                if (node.equals("*") || node.equals("/") || node.equals("^") || node.equals("%")) {
                    String preNode = list.get(i - 1);
                    String nextNode = list.get(i + 1);

                    String rs = calcutorSimple(preNode, nextNode, node);
                    list.remove(i - 1);
                    list.remove(i - 1);
                    list.remove(i - 1);

                    list.add(i - 1, rs);
                } else {
                    i++;
                }
            }

            // thực hiện các phép tính cộgn trừ
            i = 0;
            while (i < list.size()) {
                String node = list.get(i);
                if (operaList.contains(node)) {
                    if (node.equals("-") && i == 0) {
                        String nextNode = list.get(i + 1);
                        if (isNumber(nextNode)) {
                            list.remove(0);
                            list.set(0, calcutorSimple("0", nextNode, node));
                        } else {
                            viewModel.showNotification("Biểu thức lỗi");
                        }
                    } else {
                        String preNode = list.get(i - 1);
                        String nextNode = list.get(i + 1);

                        String rs = calcutorSimple(preNode, nextNode, node);
                        list.remove(i - 1);
                        list.remove(i - 1);
                        list.remove(i - 1);

                        list.add(i - 1, rs);
                    }
                } else {
                    i++;
                }
            }

        } catch (Exception e) {
            viewModel.showNotification("Biểu thức lỗi");
            e.printStackTrace();
        }

        String result = list.get(0);
        if(result.equals("A")) result = A;
        else if(result.equals("B")) result = B;
        else if(result.equals("C")) result = C;
        else if(result.equals("ANS")) result = ANS;

        return result;
    }

    public String calcutorSimple(String str1, String str2, String opera) {
        Double result = 0d;

        str1 = str1.equalsIgnoreCase("A") ? A : str1;
        str1 = str1.equalsIgnoreCase("B") ? B : str1;
        str1 = str1.equalsIgnoreCase("C") ? C : str1;
        str1 = str1.equalsIgnoreCase("ANS") ? ANS : str1;

        str2 = str2.equalsIgnoreCase("A") ? A : str2;
        str2 = str2.equalsIgnoreCase("B") ? B : str2;
        str2 = str2.equalsIgnoreCase("C") ? C : str2;
        str2 = str2.equalsIgnoreCase("ANS") ? ANS : str2;

        System.out.println("-------");
        System.out.println(str1);
        System.out.println(opera);
        System.out.println(str2);

        try {
            Double a = Double.valueOf(str1);
            Double b = 0d;
            if (!str2.isEmpty()) {
                b = Double.valueOf(str2);
            }

            switch (opera) {
                case "+":
                    result = a + b;
                    break;
                case "-":
                    result = a - b;
                    break;
                case "*":
                    result = a * b;
                    break;
                case "/":
                    result = a / b;
                    break;
                case "%":
                    result = a % b;
                    break;
                case "^":
                    result = Math.pow(a, b);
                    break;
                case "sin":
                    result = Math.sin(a);
                    break;
                case "cos":
                    result = Math.cos(a);
                    break;
                case "tan":
                    result = Math.tan(a);
                    break;
                case "sqrt":
                    result = Math.sqrt(a + b);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            viewModel.showNotification("Biểu thức lỗi");
        }
        return result.toString();
    }

    public void addToNodeList(String str) {
        // click key
        if(keyList.contains(str) && !str.equals("ANS")){
            if(SET_FLAG){
                if(nodeList.size()==1 && isNumber(nodeList.get(0))){
                    String value = nodeList.get(0);
                    if(str.equals("A")) A = value;
                    else if(str.equals("B")) B = value;
                    else if(str.equals("C")) C = value;
                    nodeList.clear();
                    cancelSet();
                } else {
                    cancelSet();
                }
            }
        }
        // click SET
        if(str.equals(SET)){
            SET_FLAG = !SET_FLAG;
            viewModel.setSET_FLAG(SET_FLAG);
        }
        // click AC
        else if(str.equals(AC)){
            nodeList.clear();
            viewModel.showExpression(showCalculation());
        }
        // click delete
        else if (str.equals(DEL)) {
            if(SET_FLAG) cancelSet();
            if (!nodeList.isEmpty()) {
                String topNode = nodeList.get(nodeList.size() - 1);
                if (isNumber(topNode)) {
                    // nếu số có 1 chữ số thì xóa luôn
                    if (topNode.length() == 1) nodeList.remove(nodeList.size() - 1);
                    else {// nếu số có nhiều chữ số thì xóa chữ số cuối cùng
                        String newNumber = topNode.substring(0, topNode.length() - 1);
                        nodeList.set(nodeList.size() - 1, newNumber);
                    }
                } else {
                    // không phải là số
                    nodeList.remove(nodeList.size() - 1);
                }
                viewModel.showExpression(showCalculation());
            }
        }
        // click RESULT
        else if (str.equals(RESULT)) {
            if(SET_FLAG) cancelSet();
            if (nodeList.isEmpty()) {
                viewModel.showNotification("Chưa nhập phép tính");
                return;
            }
            String result = calcutor(nodeList);

            if (result != "") {
                ANS = result;
                viewModel.showResult(nodeList, result);
                nodeList.clear();
                nodeList.add(result);
            }
        } // node đầu tiên
        else if (nodeList.isEmpty()) {
            if(SET_FLAG) cancelSet();
            if (numberList.contains(str) || keyList.contains(str) || str.equals("(") || str.equals("-")) {
                nodeList.add(str);
            } else if (str.equals("sin") || str.equals("cos") || str.equals("tan") || str.equals("sqrt")) {
                nodeList.add(str);
                nodeList.add("(");
            } else {
                viewModel.showNotification("Input không hợp lệ: " + str);
            }
            viewModel.showExpression(showCalculation());
        } // không phải node đầu tiên
        else {
            if(SET_FLAG) cancelSet();
            int top = nodeList.size() - 1;
            String topNode = nodeList.get(top);

            // số: {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"}
            // opera : {".", "+", "-", "*", "/", "%", "(", ")", "^", "sin", "cos", "tan", "sqrt"}
            // key : {"A", "B", "C", "ANS"}
            // nếu top node là số
            if (isNumber(topNode)) {
                if (isNumber(str)) {
                    topNode = topNode + str;
                    nodeList.set(top, topNode);
                } else if (isOpera(str)) {
                    if (str.equals(".") || str.equals("+") || str.equals("-") || str.equals("*") || str.equals("/")
                            || str.equals("%") || str.equals("^")) {
                        nodeList.add(str);
                    } else if (str.equals("sin") || str.equals("cos") || str.equals("tan") || str.equals("sqrt")) {
                        nodeList.add("*");
                        nodeList.add(str);
                        nodeList.add("(");
                    } else if (str.equals("(")) {
                        nodeList.add("*");
                        nodeList.add(str);
                    } else if (str.equals(")")) {
                        if (canClose()) {
                            nodeList.add(str);
                        } else {
                            viewModel.showNotification("Input không hợp lệ: " + str);
                        }
                    } else {
                        viewModel.showNotification("Input không hợp lệ: " + str);
                    }
                } else if (isKey(str)) {
                    nodeList.add("*");
                    nodeList.add(str);
                } else {
                    viewModel.showNotification("Input không hợp lệ: " + str);
                }
            } // nếu top node là opera
            // số: {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"}
            // opera : {".", "+", "-", "*", "/", "%", "^", "(", ")", "sin", "cos", "tan", "sqrt"}
            // key : {"A", "B", "C", "ANS"}
            else if (isOpera(topNode)) {
                if (isNumber(str)) {
                    if (topNode.equals(".")) {
                        String preNode = nodeList.get(top - 1);
                        nodeList.remove(top);
                        nodeList.set(top - 1, preNode + "." + str);
                    } else if (topNode.equals("+") || topNode.equals("-") || topNode.equals("*") || topNode.equals("/")
                            || topNode.equals("%") || topNode.equals("(") || topNode.equals("^")) {
                        nodeList.add(str);
                    } else if (topNode.equals(")")) {
                        nodeList.add("*");
                        nodeList.add(str);
                    } else if (topNode.equals("sin") || topNode.equals("cos") || topNode.equals("tan") || topNode.equals("sqrt")) {
                        nodeList.add("(");
                        nodeList.add(str);
                    }
                } else if (isOpera(str)) {
                    // nếu thêm hành động vào sau hành động
                    if (topNode.equals(".") || topNode.equals("+") || topNode.equals("-") || topNode.equals("*") || topNode.equals("/")
                            || topNode.equals("%") || topNode.equals("^")) {
                        if (str.equals(".") || str.equals("+") || str.equals("-") || str.equals("*") || str.equals("/")
                                || str.equals("%") || str.equals("^")) {
                            nodeList.remove(top);
                            nodeList.add(str);
                        } else if (str.endsWith("sin") || str.endsWith("cos") || str.endsWith("tan")
                                || str.endsWith("sqrt")) {
                            nodeList.add(str);
                            nodeList.add("(");
                        } else if (str.equals("(") && !topNode.equals(".")) {
                            nodeList.add("(");
                        } else if (str.endsWith(")")) {
                            if (!topNode.equals(".") && canClose()) {
                                nodeList.add(str);
                            } else {
                                viewModel.showNotification("Input không hợp lệ: " + str);
                            }
                        } else {
                            viewModel.showNotification("Input không hợp lệ: " + str);
                        }
                    } else if (topNode.equals("(")) {
                        if (str.equals(".") || str.equals("+") || str.equals("*") || str.equals("/")
                                || str.equals("%") || str.equals("^")) {
                            viewModel.showNotification("Input không hợp lệ: " + str);
                        } else if (str.equals("-") || str.equals("(")) {
                            nodeList.add(str);
                        } else if (str.equals("sin") || str.equals("cos") || str.equals("tan") || str.equals("sqrt")) {
                            nodeList.add(str);
                            nodeList.add("(");
                        } else if (str.equals(")")) {
                            if (canClose()) {
                                nodeList.add(str);
                            } else {
                                viewModel.showNotification("Input không hợp lệ: " + str);
                            }
                        }
                    } // số: {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"}
                    // opera : {".", "+", "-", "*", "/", "%", "^", "(", ")", "sin", "cos", "tan", "sqrt"}
                    // key : {"A", "B", "C", "ANS"}
                    else if (topNode.equals(")")) {
                        if (str.equals(".")) {
                            viewModel.showNotification("Input không hợp lệ: " + str);
                        } else if (str.equals("+") || str.equals("-") || str.equals("*") || str.equals("/") || str.equals("%")
                                || str.equals("^")) {
                            nodeList.add(str);
                        } else if (str.equals("(")) {
                            nodeList.add("*");
                            nodeList.add("(");
                        } else if (str.equals(")")) {
                            if (canClose()) {
                                nodeList.add(str);
                            } else {
                                viewModel.showNotification("Input không hợp lệ: " + str);
                            }
                        } else if (str.equals("sin") || str.equals("cos") || str.equals("tan") || str.equals("sqrt")) {
                            nodeList.add("*");
                            nodeList.add(str);
                            nodeList.add("(");
                        } else {
                            viewModel.showNotification("Input không hợp lệ: " + str);
                        }
                    } else if (topNode.equals("sin") || topNode.equals("cos") || topNode.equals("tan") || topNode.equals("sqrt")) {
                        nodeList.add("(");
                        nodeList.add(str);
                    } else {
                        viewModel.showNotification("Input không hợp lệ: " + str);
                    }
                } else if (isKey(str)) {
                    // nếu thêm key vào sau hành động
                    // opera : {".", "+", "-", "*", "/", "%", "^", "(", ")", "sin", "cos", "tan", "sqrt"}
                    // key : {"A", "B", "C", "ANS"}
                    if (topNode.equals(".")) {
                        nodeList.remove(top);
                        nodeList.add("*");
                        nodeList.add(str);
                    } else if (topNode.equals("sin") || topNode.equals("cos") || topNode.equals("tan") || topNode.equals("sqrt")) {
                        nodeList.add("(");
                        nodeList.add(str);
                    } else if (topNode.equals(")")) {
                        nodeList.add("*");
                        nodeList.add(str);
                    } else if (topNode.equals("+") || topNode.equals("-") || topNode.equals("*") || topNode.equals("/") || topNode.equals("%")
                            || topNode.equals("^") || topNode.equals("(")) {
                        nodeList.add(str);
                    } else {
                        viewModel.showNotification("Input không hợp lệ: " + str);
                    }
                } else {
                    viewModel.showNotification("Input không hợp lệ: " + str);
                }
            } // nếu top node là key
            else if (isKey(topNode)) {
                if (isNumber(str)) {
                    // thêm số vào sau key
                    nodeList.add("*");
                    nodeList.add(str);
                } else if (isOpera(str)) {
                    // thêm hành động vào sau key
                    // opera : {".", "+", "-", "*", "/", "%", "^", "(", ")", "sin", "cos", "tan", "sqrt"}
                    // key : {"A", "B", "C", "ANS"}
                    if (str.equals(".")) {
                        viewModel.showNotification("Input không hợp lệ: " + str);
                    } else if (str.equals("+") || str.equals("-") || str.equals("*") || str.equals("/")
                            || str.equals("%") || str.equals("^")) {
                        nodeList.add(str);
                    } else if (str.equals("sin") || str.equals("cos") || str.equals("tan") || str.equals("sqrt")) {
                        nodeList.add("*");
                        nodeList.add(str);
                        nodeList.add("(");
                    } else if (str.equals("(")) {
                        nodeList.add("*");
                        nodeList.add(str);
                    } else if (str.equals(")")) {
                        if (canClose()) {
                            nodeList.add(str);
                        } else {
                            viewModel.showNotification("Input không hợp lệ: " + str);
                        }
                    } else {
                        viewModel.showNotification("Input không hợp lệ: " + str);
                    }
                } else if (isKey(str)) {
                    // thêm key vào sau key
                    nodeList.add("*");
                    nodeList.add(str);
                } else {
                    viewModel.showNotification("Input không hợp lệ: " + str);
                }
            } // node nhập vào không hợp lệ
            else {
                viewModel.showNotification("Input không hợp lệ: " + str);
            }
            viewModel.showExpression(showCalculation());
        }
    }

    public void cancelSet(){
        SET_FLAG = false;
        viewModel.setSET_FLAG(false);
    }

    public String showCalculation() {
        StringBuilder str = new StringBuilder();
        for (String s : nodeList) {
            str.append(s);
        }
        System.out.println(str.toString());
        return str.toString();
    }

    public boolean canClose() {
        int open = 0;
        int close = 0;
        for (int i = 0; i < nodeList.size(); i++) {
            if (nodeList.get(i).equals("(")) {
                open++;
            } else if (nodeList.get(i).equals(")")) {
                close++;
            }
        }
        return open > close;
    }

    public boolean isNumber(String str) {
        if (str == null) {
            return false;
        }
        try {
            Double d = Double.valueOf(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isOpera(String str) {
        if (str == null) {
            return false;
        }
        return operaList.contains(str);
    }

    public boolean isKey(String str) {
        if (str == null) {
            return false;
        }
        return keyList.contains(str);
    }
}
