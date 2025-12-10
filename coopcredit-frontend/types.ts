export interface User {
  id?: number;
  username: string;
  email?: string;
}

export interface AuthResponse {
  token: string;
  username?: string; // Sometimes APIs return user details with token, or we extract from JWT
}

export interface Affiliate {
  id?: number;
  name: string;
  email: string;
  document: string;
  salary: number;
  enrollmentDate: string;
}

export enum CreditStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED'
}

export enum RiskLevel {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  CRITICAL = 'CRITICAL'
}

export interface Credit {
  id?: number;
  affiliateId: number;
  affiliateName?: string; // Optional for display if backend provides or we join
  amount: number;
  term: number;
  monthlyInstallment: number;
  status: CreditStatus;
  riskScore: number;
  riskLevel: RiskLevel;
  rationale?: string;
  createdAt: string;
}

export interface CreditRequest {
  affiliateId: number;
  amount: number;
  term: number;
}
