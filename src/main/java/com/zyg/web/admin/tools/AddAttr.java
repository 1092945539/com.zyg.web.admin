package com.zyg.web.admin.tools;

import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Map;

/**
 * Created by ZhangYaguang on 2020-02-15.
 */
public class AddAttr {
    private static Map<String, Map<String,String>> map = FindAttr.allMap();
    public static String entityNow = "";
    public static void main(String[] args) {
        File file = new File("C:\\git\\com.qs.web.gdtexAdmin\\qs_etrade\\QsTech.ETrade\\Modules\\QsTech.Permission\\Entities");//\\OrdOrderItemNode.cs
        files(file);
    }

    private static void files(File file) {
        if (file.isFile()) {
            AnalyticFile(file);
        } else {
            File[] listFiles = file.listFiles();
            if (listFiles != null){
                for (File file2 : listFiles) {
                    if (file2.isFile()) {
                        AnalyticFile(file2);
                    } else {
                        files(file2);
                    }
                }
            }
        }
    }

    private static void AnalyticFile(File file) {
        String fileName = file.getName();
        if (!".cs".equals(fileName.substring(fileName.length() - 3, fileName.length()))) {
            return;
        }
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String s;
            // 是否修改过
            boolean change = false;
            // 在第一个using后边加上引入包
            boolean fistUsing = true;
            // 最后一个注解的下标
            int lastDescriptionLength = 0;
            // 是否是Entity
            boolean isEntity = false;
            // 注释内容
            String zhushi = "";
            while ((s = br.readLine()) != null) {
                if (s.contains("[Description(\"\")]")) {
                    continue;
                }
                if (s.contains("class") && s.contains("IEntity")) {
                    // 文件中class未继承IEntity,不能直接跳过该文件，避免文件中存在多个class的问题
                    isEntity = true;
                    entityNow = s;
                    zhushi = "";
                }
                if (s.contains("using") && s.contains("System") && s.contains("ComponentModel")) {
                    // 已经引过包
                    continue;
                }
                if (s.contains("using")) {
                    // 加入引用的包
                    sb.append(s);
                    sb.append("\r\n");
                    if (fistUsing) {
                        // 如果当前为第一个using，把引入的ComponentModel包放入该注解后
                        sb.append("using System.ComponentModel;");
                        sb.append("\r\n");
                        fistUsing = false;
                    }
                    continue;
                }
                if (s.contains("///") && !s.contains("summary")) {
                    // 记录所写的注释内容
                    zhushi += s.replaceAll("///", "").trim();
                    sb.append(s);
                    sb.append("\r\n");
                    continue;
                }
                if (s.contains("public") && s.contains("get") && s.contains("set")) {
                    // 上一个属性至本属性添加前，如果已有[Description]注解，只加当前内容
                    if (sb.substring(lastDescriptionLength, sb.length()).contains("Description")) {
                        sb.append(s);
                        sb.append("\r\n");
                        zhushi = "";
                        lastDescriptionLength = sb.length();
                        continue;
                    }
                    if (!s.trim().startsWith("//")) {
                        // 注释有内容，加，注解加注释
                        if ("".equals(zhushi)) {
                            // 注释为空，解析当前的属性，
                            String p = FindAttr.AnalyticProperties(s);
                            zhushi = AnalyticProperties(p);
                            if (StringUtils.isEmpty(zhushi)){
                                zhushi = FindAttr.allZhushi(map,p);
                            }
                        }
                        sb.append("\t\t[Description(\"").append(zhushi).append("\")]");
                        sb.append("\r\n");
                    }
                    sb.append(s);
                    sb.append("\r\n");
                    // 完成后，注释内容变空，重写文件
                    zhushi = "";
                    change = true;
                    // 最后的注解下标，更新
                    lastDescriptionLength = sb.length();
                    continue;
                }
                // 匹配不到规则，原行内容
                sb.append(s);
                sb.append("\r\n");
            }
            br.close();
            if (change && isEntity) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write(sb.toString());
                bw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String AnalyticProperties(String s) {
        switch (s) {
            case "CreatorId":
                return "创建人ID";
            case "CreatorName":
                return "创建人名称";
            case "CreateDate":
                return "创建时间";
            case "ModifierId":
                return "最后修改人ID";
            case "ModifierName":
                return "最后修改人名称";
            case "ModifyDate":
                return "最后修改时间";
            case "DeletorId":
                return "删除人ID";
            case "DeleteDate":
                return "删除时间";
            case "DeletorName":
                return "删除人名称";
            case "Deleted":
                return "删除标识";
            case "Remark":
                return "备注";
            case "Currency":
                return "币别";
            case "TitleId":
                return "抬头ID";
            case "TitleName":
                return "抬头名称";
            case "OrderTitleId":
                return "订单抬头ID";
            case "OrderTitleName":
                return "订单抬头名称";
            case "TitleNameCn":
                return "抬头名称中文";
            case "Id":
                return "Id";
            case "Status":
                return "状态";
            case "AccountId":
                return "客户ID";
            case "AccountName":
                return "客户名称";
            case "SaleOrder":
                return "外销发票号";
            case "OrderId":
                return "订单ID";
            case "OrderCode":
                return "订单编码";
            case "OrderType":
                return "订单类型";
            case "OrderExportId":
                return "订单出口ID";
            case "SubApproverId":
                return "提交审批人ID";
            case "SubApproverName":
                return "提交审批人名称";
            case "SubApproveDate":
                return "提交审批人时间";
            case "SubmitId":
                return "提交人ID";
            case "SubmitName":
                return "提交人名称";
            case "SubmitDate":
            case "SubmitTime":
                return "提交时间";
            case "AcceptId":
                return "接收人ID";
            case "AcceptName":
                return "接收人名称";
            case "AcceptDate":
                return "接收时间";
            case "ClerkId":
                return "客服ID";
            case "ClerkName":
                return "客服名称";
            case "ClerkDeptId":
                return "客服部门ID";
            case "ClerkDeptName":
                return "客服部门名称";
            case "SalesId":
                return "业务员ID";
            case "SalesName":
                return "业务员名称";
            case "CreatorDeptId":
                return "创建人部门ID";
            case "CreatorDeptName":
                return "创建人部门名称";
            case "ModifierDeptId":
                return "最后修改人部门ID";
            case "ModifierDeptName":
                return "最后修改人部门名称";
            case "ApproverId":
                return "审批人ID";
            case "ApproverName":
                return "审批人名称";
            case "ApproveDate":
                return "审批时间";
            case "AccountCode":
                return "客户编码";
            case "AccountAddress":
                return "客户地址";
            case "SalesDeptId":
                return "业务员部门ID";
            case "SalesDeptName":
                return "业务员部门名称";
            case "ForeignId":
                return "外商ID";
            case "ForeignName":
                return "外商名称";
            case "AccountUserId":
                return "客户关联用户id";
            case "AccountUserName":
                return "客户关联用户名称";
            case "AccountUserDeptId":
                return "客户关联用户部门ID";
            case "AccountUserDeptName":
                return "客户关联用户部门名称";
            case "LogisiticsClerkId":
                return "物流客服ID";
            case "LogisiticsClerkName":
                return "物流客服名称";
            case "Version__":
                return "版本号";
            case "Quantity":
                return "数量";
            case "Type":
                return "类型";
            case "HsCode":
                return "海关编码";
            case "Specification":
                return "规格/型号";
            case "NWeight":
            case "NetWeight":
                return "净重";
            case "SalesOrder":
                return "外销发票号";
            case "GWeight":
            case "GrossWeight":
                return "毛重";
            case "BuyerId":
                return "买方ID";
            case "BuyerName":
                return "买方名称";
            case "BuyerAddress":
                return "买方地址";
            case "BuyerPhone":
                return "买方电话";
            case "BankId":
                return "银行ID";
            case "BankName":
                return "银行名称";
            case "Spjc":
                return "审批进程";
            case "RmbRate":
            case "RateRmb":
                return "RMB汇率";
            case "UsdRate":
            case "RateUsd":
                return "USD汇率";
            case "SysCompanyId":
                return "公司ID";
            case "SysCompanyName":
                return "公司名称";
            case "SellerId":
                return "卖方ID";
            case "SellerName":
                return "卖方名称";
            case "SellerPhone":
                return "卖方电话";
            case "AgencyFee":
                return "代理费";
            case "Email":
                return "邮箱";
            case "Tel":
                return "电话";
            case "File":
                return "附件";
            case "FileId":
                return "附件ID";
            case "FileName":
                return "附件名称";
            case "AttachmentTypeId":
                return "附件类型ID";
            case "Seq":
                return "序号";
            case "VatRate":
                return "增税率";
            case "RetRate":
                return "退税率";
            case "SettleId":
            case "SettlementId":
                return "服务项结算公式ID";
            case "SettleName":
            case "SettlementName":
                return "服务项结算公式名称";
            case "FirstQuantity":
                return "第一数量";
            case "FirstUnit":
            case "FirstQuantityUnit":
                return "第一单位";
            case "FirstUnitId":
                return "第一单位Id";
            case "FirstUnitCn":
            case "FirstUnitDetailCn":
                return "第一单位中文";
            case "FirstUnitEn":
            case "FirstUnitDetailEn":
                return "第一单位英文";
            case "SecondQuantity":
                return "第二数量";
            case "SecondUnit":
            case "SecondQuantityUnit":
                return "第二单位";
            case "SecondUnitId":
                return "第二单位Id";
            case "SecondUnitCn":
            case "SecondUnitDetailCn":
                return "第二单位中文";
            case "SecondUnitEn":
            case "SecondUnitDetailEn":
                return "第二单位中文";
            case "ServiceItemId":
                return "服务项类别ID";
            case "ServiceItemName":
                return "服务项类别名称";
            case "DeductType":
                return "服务项费用的扣除来源";
            case "Amount":
                return "金额";
            case "RmbAmount":
            case "AmountRmb":
                return "RMB金额";
            case "UsdAmount":
            case "AmountUsd":
                return "USD金额";
            case "Code":
                return "编码";
            case "Unit":
                return "计量单位";
            case "UnitId":
                return "计量单位ID";
            case "UnitEn":
                return "计量单位英文";
            case "UnitCn":
                return "计量单位中文";
            case "UnitInfo":
                return "计量单位";
            case "Price":
                return "单位";
            case "WeightUnit":
                return "重量单位";
            case "WeightUnitId":
                return "重量单位ID";
            case "WeightUnitCn":
                return "重量单位中文";
            case "WeightUnitEn":
                return "重量单位英文";
            case "Name":
                return "名称";
            case "NameEn":
                return "名称英文";
            case "DestinationPort":
                return "目的港";
            case "DestinationPortCn":
                return "目的港中文";
            case "DestinationPortEn":
                return "目的港英文";
            case "PackUnitCn":
                return "包装单位中文";
            case "PackUnit":
                return "包装单位";
            case "PackQuantity":
                return "包装单位数量";
            case "PackUnitId":
                return "包装单位ID";
            case "PackUnitEn":
                return "包装单位英文";
            case "LeftQuantity":
                return "剩余数量";
            case "LeftAmount":
                return "剩余金额";
            case "UnitNWeight":
                return "每件净重";
            case "UnitGWeight":
                return "每件毛重";
            case "OrderDetailId":
                return "订单明细ID";
            case "ContractId":
                return "合同ID";
            case "ContractNo":
                return "合同号";
            case "SalesContractId":
                return "销售合同ID";
            case "SalesContractNo":
                return "销售合同号";
            case "CustomsAmount":
                return "报关金额";
            case "CustomsTaxRate":
                return "海关税率";
            case "FilePath":
                return "附件路径";
            case "DepartmentId":
                return "部门ID";
            case "DepartmentName":
                return "部门名称";
            case "ModeOfTrade":
                return "贸易方式";
            case "SupplierId":
                return "供应商ID";
            case "SupplierName":
                return "供应商名称";
            case "AccountContacts":
            case "AccountContactor":
                return "客户联系人";
            case "AccountContactPhone":
                return "客户联系电话";
            case "AccountContactMobilePhone":
                return "客户联系手机";
            case "AccountContactQq":
                return "客户联系QQ";
            case "AccountContactSkype":
                return "客户联系Skype";
            case "AccountEmail":
                return "客户邮箱";
            case "ServiceItemType":
                return "服务项类别";
            case "SettleItemType":
                return "服务项结算公式";
            case "OrderAmount":
                return "订单金额";
            case "OrderCurrency":
                return "订单币别";
            case "SpecialClause":
                return "特约条款";
            case "GeneralTermsConditions":
                return "一般条款";
            case "ImpExpDate":
                return "出口日期";
            case "ModeOfTransportation":
                return "运输方式";
            case "Brand":return"品牌";
            case "OperatorId":return "经营单位ID";
            case "OperatorName":return "经营单位名称";
            case "WarehouseId":return"仓库ID";
            case "WarehouseName":return"仓库名称";
            case "StorageDate":return"入仓日期";
            case "OperatorDeptId":return "经营单位部门ID";
            case "OperatorDeptName":return "经营单位部门名称";
            case "ProtocolId":return "协议ID";
            case "ProtocolCode":return "协议编码";
            case "ExportPort":return "出口港";
            case "ExportPortCn":return "出口港中文";
            case "ExportPortEn":return "出口港英文";
            case "TransshippmentPort":return "中转港";
            case "TransshippmentPortCn":return "中转港中文";
            case "TransshippmentPortEn":return "中转港英文";
            case "PriceTerm":return "价格条款";
            case "PriceTermDesc":return "价格条款说明";
            case "Vol":return "体积";
            case "VolUnit":return "体积单位";
            case "VolUnitId":return "体积单位ID";
            case "VolUnitCn":return "体积单位中文";
            case "VolUnitEn":return "体积单位英文";
            case "ModeOfPacking":return "包装方式";
            case "FinalDestCountry":return "最终目的港";
            case "FinalDestCountryId":return "最终目的港ID";
            case "FinalDestCountryCn":return "最终目的港中文";
            case "FinalDestCountryEn":return "最终目的港英文";
            case "PackingQuantity":return "包装数量";
            case "PackingUnit":return "包装单位";
            case "PackingUnitId":return "包装单位ID";
            case "PackingUnitCn":return "包装单位中文";
            case "PackingUnitEn":return "包装单位英文";
            case "BlNo":return "提单号";
            case "SettleFlag":return "结算标志";
            case "CostId":return "费用ID";
            case "CostName":return "费用名称";
            case "OccurDate":return "发生日期";
            case "RelatedCompanyId":return "往来单位ID";
            case "Deadline":return "截止日期";
            case "DraweerId":
            case "DraweeId":return "开票单位ID";
            case "DraweerName":return "开票单位名称";
            case "BankSlip":return "水单";
            case "BankSlipId":return "水单ID";
            case "BankSlipCode":return "水单编码";
            case "OriginalCurrency":return "原币币别";
            case "OriginalAmount":return "原币金额";
            case "Balance":return "余额";
            case "ActualUsdAmount":return "实际USD金额";
            case "ActualUsdRate":return "实际USD汇率";
            case "ActualRmbAmount":return "实际RMB金额";
            case "ActualRmbRate":return "实际RMB汇率";
            case "OrderReceiptCompleted":return "订单拆分是否完成";
            case "BatchNo":return "批次号";
            case "SendTime":return "发送时间";
            case "FinanceApproverId":return "财务审核人ID";
            case "FinanceApproverName":return "财务审核人名称";
            case "FinanceApproverDeptId":return "财务审核人部门ID";
            case "FinanceApproverDeptName":return "财务审核人部门名称";
            case "FinanceApproveDate":return "财务审核时间";
            case "StartDate":return "起始日期";
            case "EndDate":return "结束日期";
            case "ApprovedProcess":return "审批进程";
            case "BusinessType":return "业务类型";
            case "ReceiptUsdRate":return "增票USD汇率";
            case "ReceiptUsdAmount":return "增票USD金额";
            case "ReceiptRmbRate":return "增票RMB汇率";
            case "ReceiptRmbAmount":return "增票RMB金额";
            case "OrderUsdRate":return "订单USD汇率";
            case "OrderUsdAmount":return "订单USD金额";
            case "OrderRmbRate":return "订单RMB汇率";
            case "OrderRmbAmount":return "订单RMB金额";
            case "PaymentType":return "付款类型";
//		case "":return "";
//		case "":return "";
            default:
                return "";
        }
    }
}
